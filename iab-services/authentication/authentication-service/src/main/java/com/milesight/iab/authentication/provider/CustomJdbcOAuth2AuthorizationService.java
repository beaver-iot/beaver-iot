package com.milesight.iab.authentication.provider;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * @author loong
 * @date 2024/10/29 10:16
 */
public class CustomJdbcOAuth2AuthorizationService extends JdbcOAuth2AuthorizationService implements CustomOAuth2AuthorizationService {

    private static final String TABLE_NAME = "oauth2_authorization";

    private static final String PRINCIPAL_FILTER = "principal_name = ?";
    private static final String REMOVE_PRINCIPAL_SQL = "DELETE FROM " + TABLE_NAME + " WHERE " + PRINCIPAL_FILTER;

    public CustomJdbcOAuth2AuthorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        super(jdbcOperations, registeredClientRepository);
    }

    @Override
    public void removeByPrincipalName(String principalName) {
        //FIXME Temporarily allow the same user to generate multiple valid tokens
//        Assert.notNull(principalName, "principalName cannot be null");
//        SqlParameterValue[] parameters = new SqlParameterValue[] {
//                new SqlParameterValue(Types.VARCHAR, principalName) };
//        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters);
//        getJdbcOperations().update(REMOVE_PRINCIPAL_SQL, pss);
    }

}
