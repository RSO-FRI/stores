package si.fri.rso.samples.imagecatalog.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.imagecatalog.lib.Store;
import si.fri.rso.samples.imagecatalog.models.converters.StoreConverter;
import si.fri.rso.samples.imagecatalog.models.entities.StoreEntity;


@RequestScoped
public class StoreDataBean {

    private Logger log = Logger.getLogger(StoreDataBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Store> getStoreData() {

        TypedQuery<StoreEntity> query = em.createNamedQuery(
                "StoreEntity.getAll", StoreEntity.class);

        List<StoreEntity> resultList = query.getResultList();

        return resultList.stream().map(StoreConverter::toDto).collect(Collectors.toList());

    }

    @Timed
    public List<Store> getStoreDataFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, StoreEntity.class, queryParameters).stream()
                .map(StoreConverter::toDto).collect(Collectors.toList());
    }

    public Store getStoreData(Integer id) {

        StoreEntity storeEntity = em.find(StoreEntity.class, id);

        if (storeEntity == null) {
            throw new NotFoundException();
        }

        Store storeData = StoreConverter.toDto(storeEntity);

        return storeData;
    }

    public Store createStore(Store store) {

        StoreEntity imageMetadataEntity = StoreConverter.toEntity(store);

        try {
            beginTx();
            em.persist(imageMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (imageMetadataEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return StoreConverter.toDto(imageMetadataEntity);
    }

    public Store putStoreData(Integer id, Store store) {

        StoreEntity c = em.find(StoreEntity.class, id);

        if (c == null) {
            return null;
        }

        StoreEntity storeEntity = StoreConverter.toEntity(store);

        try {
            beginTx();
            storeEntity.setId(c.getId());
            storeEntity = em.merge(storeEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return StoreConverter.toDto(storeEntity);
    }

    public boolean deleteStoreData(Integer id) {

        StoreEntity storeEntity = em.find(StoreEntity.class, id);

        if (storeEntity != null) {
            try {
                beginTx();
                em.remove(storeEntity);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
