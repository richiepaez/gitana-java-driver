/**
 * Copyright 2010 Gitana Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information, please contact Gitana Software, Inc. at this
 * address:
 *
 *   info@gitanasoftware.com
 */

package org.gitana.platform.client.registrar;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.billing.PaymentMethodValidation;
import org.gitana.platform.client.plan.Plan;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class RegistrarImpl extends AbstractPlatformDataStoreImpl implements Registrar
{
    public RegistrarImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getType()
    {
        return "registrar";
    }

    @Override
    public void reload()
    {
        Response response = getRemote().get(getResourceUri());

        Registrar registrar = getFactory().registrar(getPlatform(), response);
        this.reload(registrar);
    }

    @Override
    public String getResourceUri()
    {
        return "/registrars/" + getId();
    }


    @Override
    public ResultMap<Tenant> listTenants()
    {
        return listTenants(null);
    }

    @Override
    public ResultMap<Tenant> listTenants(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/tenants", params);
        return getFactory().tenants(this, response);
    }

    @Override
    public ResultMap<Tenant> queryTenants(ObjectNode query)
    {
        return queryTenants(query, null);
    }

    @Override
    public ResultMap<Tenant> queryTenants(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/tenants/query", params, query);
        return getFactory().tenants(this, response);
    }

    @Override
    public Tenant readTenant(String tenantId)
    {
        Tenant tenant = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/tenants/" + tenantId);
            tenant = getFactory().tenant(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return tenant;
    }

    @Override
    public Tenant lookupTenant(DomainPrincipal principal)
    {
        Tenant tenant = null;

        String principalIdentifier = principal.getString("domainId") + "/" + principal.getId();
        try
        {
            Response response = getRemote().get(getResourceUri() + "/tenants/lookup?id" + principalIdentifier);
            tenant = getFactory().tenant(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return tenant;
    }

    @Override
    public Tenant createTenant(DomainPrincipal principal, String planKey)
    {
        return createTenant(principal, planKey, null);
    }

    @Override
    public Tenant createTenant(DomainPrincipal principal, String planKey, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put("domainId", principal.getString("domainId"));
        object.put("principalId", principal.getId());
        object.put("planKey", planKey);

        Response response = getRemote().post(getResourceUri() + "/tenants", object);

        String tenantId = response.getId();
        return readTenant(tenantId);
    }

    @Override
    public void updateTenant(Tenant tenant)
    {
        getRemote().put(getResourceUri() + "/tenants/" + tenant.getId(), tenant.getObject());
    }

    @Override
    public void deleteTenant(Tenant tenant)
    {
        deleteTenant(tenant.getId());
    }

    @Override
    public void deleteTenant(String tenantId)
    {
        getRemote().delete(getResourceUri() + "/tenants/" + tenantId);
    }

    @Override
    public ResultMap<Plan> listPlans()
    {
        return listPlans(null);
    }

    @Override
    public ResultMap<Plan> listPlans(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/plans", params);
        return getFactory().plans(this, response);
    }

    @Override
    public ResultMap<Plan> queryPlans(ObjectNode query)
    {
        return queryPlans(query, null);
    }

    @Override
    public ResultMap<Plan> queryPlans(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/plans/query", params, query);
        return getFactory().plans(this, response);
    }

    @Override
    public Plan readPlan(String planId)
    {
        Plan plan = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/plans/" + planId);
            plan = getFactory().plan(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return plan;
    }

    @Override
    public Plan createPlan(String planKey)
    {
        return createPlan(planKey, null);
    }

    @Override
    public Plan createPlan(String planKey, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put("planKey", planKey);

        Response response = getRemote().post(getResourceUri() + "/plans", object);

        return readPlan(planKey);
    }

    @Override
    public void updatePlan(Plan plan)
    {
        getRemote().put(getResourceUri() + "/plans/" + plan.getId(), plan.getObject());
    }

    @Override
    public void deletePlan(Plan plan)
    {
        deletePlan(plan.getId());
    }

    @Override
    public void deletePlan(String planKey)
    {
        getRemote().delete(getResourceUri() + "/plans/" + planKey);
    }

    @Override
    public ResultMap<Tenant> findTenantsWithPrincipalTeamMember(DomainPrincipal principal, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);
        params.put("id", principal.getDomainQualifiedId());

        Response response = getRemote().post(getResourceUri() + "/tenants/withmember", params);
        return getFactory().tenants(this, response);
    }

    @Override
    public PaymentMethodValidation validateCreditCard(String holderName, String number, int expirationMonth, int expirationYear)
    {
        ObjectNode object = JsonUtil.createObject();

        // TODO: populate

        return new PaymentMethodValidation(object);
    }

    @Override
    public PaymentMethodValidation validateCreditCard(ObjectNode object)
    {
        // TODO: run validation
        return new PaymentMethodValidation(object);
    }
    
}