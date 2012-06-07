package com.proofpoint.sampleproject;

import com.google.common.base.Preconditions;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@Path("/v1/test")
public class TestResource
{
    private final TestManager testManager;

    @Inject
    public TestResource(TestManager testManager)
    {
        Preconditions.checkNotNull(testManager, "testManager must not be null");

        this.testManager = testManager;
    }

    @POST
    public Response runTests(InputStream xmlSuite)
            throws IOException, SAXException, ParserConfigurationException
    {
        if (!testManager.run(xmlSuite)) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Tests are currently running").build();
        }
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testStatus()
    {
        return Response.ok().entity(testManager.toWire()).build();
    }

}

