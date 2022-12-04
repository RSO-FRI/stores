package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.api.v1.dtos.UploadImageResponse;
import si.fri.rso.samples.imagecatalog.lib.Store;
import si.fri.rso.samples.imagecatalog.services.beans.StoreDataBean;
import si.fri.rso.samples.imagecatalog.services.clients.AmazonRekognitionClient;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Log
@ApplicationScoped
@Path("/stores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StoreDataResource {

    private Logger log = Logger.getLogger(StoreDataResource.class.getName());

    @Inject
    private StoreDataBean storeDataBean;


    @Context
    protected UriInfo uriInfo;

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    @Operation(description = "Get data for all stores.", summary = "Get all stores")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of store data",
                    content = @Content(schema = @Schema(implementation = Store.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getStoreData() {

        List<Store> storeData = storeDataBean.getStoreDataFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(storeData).build();
    }


    @Operation(description = "Get data for a store.", summary = "Get data for a store")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Store data",
                    content = @Content(
                            schema = @Schema(implementation = Store.class))
            )})
    @GET
    @Path("/{storeId}")
    public Response getStoreData(@Parameter(description = "Store ID.", required = true)
                                     @PathParam("storeId") Integer storeId) {

        Store storeData = storeDataBean.getStoreData(storeId);

        if (storeData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(storeData).build();
    }

    @Operation(description = "Add store data.", summary = "Add data")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Store data successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createStoreData(@RequestBody(
            description = "DTO object with store data.",
            required = true, content = @Content(
            schema = @Schema(implementation = Store.class))) Store storeData) {

        if ((storeData.getTitle() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            storeData = storeDataBean.createStore(storeData);
        }

        return Response.status(Response.Status.CONFLICT).entity(storeData).build();

    }


    @Operation(description = "Update data for a store.", summary = "Update store data")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Data successfully updated."
            )
    })
    @PUT
    @Path("{storeId}")
    public Response putStoreData(@Parameter(description = "Store ID.", required = true)
                                     @PathParam("storeId") Integer storeId,
                                 @RequestBody(
                                             description = "DTO object with store data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = Store.class)))
                                     Store storeData){

        storeData = storeDataBean.putStoreData(storeId, storeData);

        if (storeData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @Operation(description = "Delete data for a store.", summary = "Delete data")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Data successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{storeId}")
    public Response deleteStoreData(@Parameter(description = "Store ID.", required = true)
                                        @PathParam("storeId") Integer storeId){

        boolean deleted = storeDataBean.deleteStoreData(storeId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
