package org.zhuboss.pc2server.pcproxy.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2016年1月4日 下午4:06:01 
 * 类说明 
 */
public class NoSSLValidateHttpClient extends DefaultHttpClient{

	NoSSLValidateHttpClient() throws Exception {
        super();
        noValidate();
    }


	public NoSSLValidateHttpClient(ClientConnectionManager conman,
			HttpParams params) throws Exception{
		super(conman, params);
		 noValidate();
	}


	public NoSSLValidateHttpClient(ClientConnectionManager conman) throws Exception{
		super(conman);
		 noValidate();
	}


	public NoSSLValidateHttpClient(HttpParams params) throws Exception{
		super(params);
		 noValidate();
	}


	private void noValidate() throws NoSuchAlgorithmException,
			KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new    SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
	}

	
}
