package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.Store;
import si.fri.rso.samples.imagecatalog.models.entities.StoreEntity;

public class StoreConverter {

    public static Store toDto(StoreEntity entity) {

        Store dto = new Store();
        dto.setStoreId(entity.getId());
        dto.setTitle(entity.getTitle());

        return dto;

    }

    public static StoreEntity toEntity(Store dto) {

        StoreEntity entity = new StoreEntity();
        entity.setTitle(dto.getTitle());

        return entity;

    }

}
