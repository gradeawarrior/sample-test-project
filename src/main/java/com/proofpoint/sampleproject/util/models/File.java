package com.proofpoint.sampleproject.util.models;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.UUID;

public class File
{
    public static final String BASIC_FILE_DATA = "Hello World!";
    public static final String BASIC_FILE_TYPE = "text/plain";
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final UUID id;
    private final String type;
    private final String name;
    private byte[] data;

    public File()
    {
        id = UUID.randomUUID();
        type = BASIC_FILE_TYPE;
        name = "basic-file-data.txt";
        data = BASIC_FILE_DATA.getBytes();
    }

    public File(String name, String type)
    {
        id = UUID.randomUUID();
        this.type = type;
        this.name = name;
        this.data = BASIC_FILE_DATA.getBytes();
    }

    public File(UUID id, String name, String type)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.data = BASIC_FILE_DATA.getBytes();
    }

    public File(UUID id, String name, String type, byte[] data)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.data = data;
    }

    public File(UUID id, String name, byte[] data)
    {
        this.id = id;
        this.type = BASIC_FILE_TYPE;
        this.name = name;
        this.data = data;
    }

    public UUID getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public byte[] getData()
    {
        return data;
    }

    public Map<String, Object> get()
    {
        return new ImmutableMap.Builder<String, Object>()
                .put("fileId", id)
                .put("filename", name)
                .put("contentType", type)
                .build();
    }
}
