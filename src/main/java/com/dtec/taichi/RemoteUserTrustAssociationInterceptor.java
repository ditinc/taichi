package com.dtec.taichi;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;
import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

public class RemoteUserTrustAssociationInterceptor implements TrustAssociationInterceptor {  
  private static final String USER_HEADER = "REMOTE_USER";

  private String proxy = null;

  public void cleanup() {}

  public String getType() {
    return this.getClass().getName();
  }

  public String getVersion() {
    return "1.0";
  }

  public int initialize(final Properties properties) throws WebTrustAssociationFailedException {
    this.proxy = properties.getProperty("proxy");
    return 0;
  }

  public boolean isTargetInterceptor(final HttpServletRequest request)
      throws WebTrustAssociationException {
    if (this.proxy != null && (!request.getRemoteAddr().equals(this.proxy)
        && !request.getRemoteHost().equals(this.proxy))) {
      return false;
    }
    final Enumeration<?> requestHeaders = request.getHeaders(USER_HEADER);
    return requestHeaders.hasMoreElements();
  }

  public TAIResult negotiateValidateandEstablishTrust(final HttpServletRequest request,
      final HttpServletResponse response) throws WebTrustAssociationFailedException {
    final Enumeration<?> requestHeaders = request.getHeaders(USER_HEADER);
    if (requestHeaders.hasMoreElements()) {
      final String userName = (String) requestHeaders.nextElement();
      return TAIResult.create(HttpServletResponse.SC_OK, userName);
    }
    return null;
  }
}
