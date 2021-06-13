package vmware.samples.common;

import com.vmware.vapi.bindings.StubFactory;
import com.vmware.vapi.cis.authn.ProtocolFactory;
import com.vmware.vapi.core.ApiProvider;
import com.vmware.vapi.protocol.HttpConfiguration;
import com.vmware.vapi.protocol.ProtocolConnection;

public class StaticHelper {


    public static final String VAPI_PATH = "/api";

    public static boolean skipServerVerification = true;

    /**
     * Connects to the server using https protocol and returns the factory
     * instance that can be used for creating the client side stubs.
     *
     * @param server hostname or ip address of the server
     * @param httpConfig HTTP configuration settings to be applied
     * for the connection to the server.
     *
     * @return factory for the client side stubs
     */
    public static StubFactory createApiStubFactory(String server,
                                            HttpConfiguration httpConfig)
            throws Exception {
        // Create a https connection with the vapi url
        ProtocolFactory pf = new ProtocolFactory();
        String apiUrl = "https://" + server + VAPI_PATH;

        // Get a connection to the vapi url
        ProtocolConnection connection =
                pf.getHttpConnection(apiUrl, null, httpConfig);

        // Initialize the stub factory with the api provider
        ApiProvider provider = connection.getApiProvider();
        StubFactory stubFactory = new StubFactory(provider);
        return stubFactory;
    }


    /**
     * Builds the Http settings to be applied for the connection to the server.
     * @return http configuration
     * @throws Exception
     */
    public static HttpConfiguration buildHttpConfiguration() throws Exception {
        HttpConfiguration httpConfig =
                new HttpConfiguration.Builder()
                        .setSslConfiguration(buildSslConfiguration())
                        .getConfig();

        return httpConfig;
    }

    /**
     * Builds the SSL configuration to be applied for the connection to the
     * server
     *
     * For vApi connections:
     * If "skip-server-verification" is specified, then the server certificate
     * verification is skipped. The method retrieves the certificate
     * from specified server and adds it to an in-memory trustStore which is
     * returned.
     * If "skip-server-verification" is not specified, then it uses the
     * truststorepath and truststorepassword to load the truststore and return
     * it.
     *
     * For VIM connections:
     * If "skip-server-verification" is specified, then it trusts all the
     * VIM API connections made to the specified server.
     * If "skip-server-verification" is not specified, then it sets the System
     * environment property "javax.net.ssl.trustStore" to the path of the file
     * containing the trusted server certificates.
     *
     *<p><b>
     * Note: Below code circumvents SSL trust if "skip-server-verification" is
     * specified. Circumventing SSL trust is unsafe and should not be used
     * in production software. It is ONLY FOR THE PURPOSE OF DEVELOPMENT
     * ENVIRONMENTS.
     *<b></p>
     * @return SSL configuration
     * @throws Exception
     */
    protected static HttpConfiguration.SslConfiguration buildSslConfiguration() throws Exception {
        HttpConfiguration.SslConfiguration sslConfig = null;

        if(skipServerVerification) {
            /*
             * Below method enables all VIM API connections to the server
             * without validating the server certificates.
             *
             * Note: Below code is to be used ONLY IN DEVELOPMENT ENVIRONMENTS.
             * Circumventing SSL trust is unsafe and should not be used in
             * production software.
             */
            SslUtil.trustAllHttpsCertificates();

            /*
             * Below code enables all vAPI connections to the server
             * without validating the server certificates..
             *
             * Note: Below code is to be used ONLY IN DEVELOPMENT ENVIRONMENTS.
             * Circumventing SSL trust is unsafe and should not be used in
             * production software.
             */
            sslConfig = new HttpConfiguration.SslConfiguration.Builder()
                    .disableCertificateValidation()
                    .disableHostnameVerification()
                    .getConfig();
        } else {
            /*
             * Set the system property "javax.net.ssl.trustStore" to
             * the truststorePath
             */
            /*
            System.setProperty("javax.net.ssl.trustStore", this.truststorePath);
            KeyStore trustStore =
                    SslUtil.loadTrustStore(this.truststorePath,
                            this.truststorePassword);
            HttpConfiguration.KeyStoreConfig keyStoreConfig =
                    new HttpConfiguration.KeyStoreConfig("", this.truststorePassword);
            sslConfig =
                    new HttpConfiguration.SslConfiguration.Builder()
                            .setKeyStore(trustStore)
                            .setKeyStoreConfig(keyStoreConfig)
                            .getConfig();
            */
        }

        return sslConfig;
    }
}
