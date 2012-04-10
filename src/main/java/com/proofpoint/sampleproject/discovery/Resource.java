package com.proofpoint.sampleproject.discovery;

import java.net.URI;
import java.util.List;

public interface Resource
{
  public List<URI> getServices();

  public URI getService();
}
