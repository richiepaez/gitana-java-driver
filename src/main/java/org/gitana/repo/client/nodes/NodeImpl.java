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

package org.gitana.repo.client.nodes;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.repo.association.Direction;
import org.gitana.repo.association.Directionality;
import org.gitana.repo.client.Attachment;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.beans.TraversalResults;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.*;

/**
 * Default "n:node" implementation for a node.
 *
 * This class is the base class for all nodes in the Gitana repository.
 * 
 * @author uzi
 */
public class NodeImpl extends BaseNodeImpl implements Node
{
    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public NodeImpl(Driver driver, Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(driver, branch, obj, isSaved);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return access control list
     */
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ATTACHMENTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadAttachment(byte[] bytes, String contentType)
    {
        uploadAttachment(null, bytes, contentType);
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;

        try
        {
            getRemote().upload(uri, bytes, contentType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType, String fileName)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;

        try
        {
            getRemote().upload(uri, bytes, contentType, fileName);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadAttachments(HttpPayload... payloads)
    {
        Map<String, String> params = new HashMap<String, String>();

        uploadAttachments(params, payloads);
    }

    @Override
    public void uploadAttachments(Map<String, String> params, HttpPayload... payloads)
    {
        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments";

        try
        {
            getRemote().upload(uri, params, payloads);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadAttachment()
    {
        return downloadAttachment(null);
    }

    @Override
    public byte[] downloadAttachment(String attachmentId)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;

        byte[] bytes = null;
        try
        {
            bytes = getRemote().downloadBytes(uri);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

    @Override
    public List<Attachment> listAttachments()
    {
        Map<String, Attachment> map = fetchAttachments();

        List<Attachment> list = new ArrayList<Attachment>();
        for (Attachment attachment : map.values())
        {
            list.add(attachment);
        }

        return list;
    }

    @Override
    public ResultMap<Attachment> fetchAttachments()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments");

        return getFactory().attachments(this, response);
    }

    @Override
    public String getDownloadUri(String attachmentId)
    {
        return "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ASSOCIATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Association> associations()
    {
        return associations((Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(Pagination pagination)
    {
        return associations(Direction.ANY, pagination);
    }

    @Override
    public ResultMap<Association> associations(Direction direction)
    {
        return associations(direction, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(Direction direction, Pagination pagination)
    {
        return associations(null, direction, pagination);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName)
    {
        return associations(associationTypeQName, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Pagination pagination)
    {
        return associations(associationTypeQName, Direction.ANY, pagination);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction)
    {
        return associations(associationTypeQName, direction, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction, Pagination pagination)
    {
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/associations";

        // build params (from pagination)
        Map<String, String> params = DriverUtil.params(pagination);
        if (direction != null)
        {
            params.put("direction", direction.toString());
        }
        if (associationTypeQName != null)
        {
            params.put("type", associationTypeQName.toString());
        }

        Response response = getRemote().get(uri, params);

        return getFactory().associations(getBranch(), response);
    }

    @Override
    public Association associate(Node targetNode, QName associationTypeQName)
    {
        return associate(targetNode, associationTypeQName, Directionality.DIRECTED);
    }

    @Override
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality)
    {
        String sourceNodeId = getId();
        String targetNodeId = otherNode.getId();

        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + sourceNodeId + "/associate?node=" + targetNodeId + "&type=" + associationTypeQName.toString();
        if (!Directionality.DIRECTED.equals(directionality))
        {
            uri += "&directionality=" + directionality.toString();
        }

        Response r1 = getRemote().post(uri);

        String associationId = r1.getId();

        // read it back
        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + associationId);
        return getFactory().association(getBranch(), r2);
    }

    @Override
    public void unassociate(Node targetNode, QName associationTypeQName)
    {
        unassociate(targetNode, associationTypeQName, Directionality.DIRECTED);
    }

    @Override
    public void unassociate(Node otherNode, QName associationTypeQName, Directionality directionality)
    {
        String sourceNodeId = getId();
        String targetNodeId = otherNode.getId();

        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + sourceNodeId + "/unassociate?node=" + targetNodeId + "&type=" + associationTypeQName.toString();
        if (!Directionality.DIRECTED.equals(directionality))
        {
            uri += "&directionality=" + directionality.toString();
        }

        getRemote().post(uri);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRAVERSE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public TraversalResults traverse(ObjectNode traverse)
    {
        ObjectNode config = JsonUtil.createObject();
        config.put("traverse", traverse);

        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/traverse";
        Response r = getRemote().post(uri, config);

        TraversalResults results = new TraversalResults();
        results.parse(getFactory(), getBranch(), r);

        return results;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MOUNT
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void mount(String mountKey)
    {
        String uri = "/repositories/" + this.getRepository().getId() + "/branches/" + this.getBranch().getId() + "/nodes/" + this.getId() + "/mount/" + mountKey;
        getRemote().post(uri);
    }

    @Override
    public void unmount()
    {
        String uri = "/repositories/" + this.getRepository().getId() + "/branches/" + this.getBranch().getId() + "/nodes/" + this.getId() + "/unmount";
        getRemote().post(uri);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRANSLATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Node createTranslation(String edition, Locale locale, ObjectNode object)
    {
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/i18n?edition=" + edition + "&locale=" + locale.toString();
        Response r1 = getRemote().post(uri, object);
        String nodeId = r1.getId();

        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + nodeId);
        return (Node) getFactory().node(getBranch(), r2);
    }

    @Override
    public List<String> getTranslationEditions()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/i18n/editions");

        ArrayNode array = (ArrayNode) response.getObjectNode().get("editions");

        List<String> editions = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++)
        {
            editions.add(array.get(i).getTextValue());
        }

        return editions;
    }

    @Override
    public List<Locale> getTranslationLocales(String edition)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/i18n/locales?edition=" + edition);

        ArrayNode array = (ArrayNode) response.getObjectNode().get("locales");

        List<Locale> locales = new ArrayList<Locale>();
        for (int i = 0; i < array.size(); i++)
        {
            String localeString = array.get(i).getTextValue();
            Locale locale = org.gitana.util.I18NUtil.parseLocale(localeString);

            locales.add(locale);

        }

        return locales;
    }

    @Override
    public Node readTranslation(Locale locale)
    {
        return readTranslation(null, locale);
    }

    @Override
    public Node readTranslation(String edition, Locale locale)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + this.getBranchId() + "/nodes/" + getId() + "/i18n?locale=" + locale.toString();
        if (edition != null)
        {
            uri += "&edition=" + edition;
        }

        Response response = getRemote().get(uri);
        return (Node) getFactory().node(getBranch(), response);
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse)
    {
        return findNodes(query, searchTerm, traverse, null);
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse, Pagination pagination)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + this.getBranchId() + "/nodes/" + getId() + "/find";

        ObjectNode payload = JsonUtil.createObject();
        if (query != null)
        {
            payload.put("query", query);
        }
        if (searchTerm != null)
        {
            payload.put("search", searchTerm);
        }
        if (traverse != null)
        {
            payload.put("traverse", traverse);
        }

        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(uri, params, payload);

        return getFactory().nodes(getBranch(), response);
    }

}
