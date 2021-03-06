/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.client.attachment;

import org.apache.http.HttpResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.Driver;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;

import java.io.InputStream;

/**
 * @author uzi
 */
public class AttachmentImpl implements Attachment
{
    private Attachable attachable;
    private ObjectNode objectNode;

    public AttachmentImpl(Attachable attachable, ObjectNode objectNode)
    {
        this.attachable = attachable;

        this.objectNode = objectNode;
    }

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    @Override
    public String getId()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_ID).textValue();
    }

    @Override
    public String getObjectId()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_OBJECT_ID).textValue();
    }

    @Override
    public long getLength()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_LENGTH).longValue();
    }

    @Override
    public String getContentType()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_CONTENT_TYPE).textValue();
    }
    
    @Override
    public String getFilename()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_FILENAME).textValue();
    }

    @Override
    public InputStream getInputStream()
    {
        InputStream in = null;

        try
        {
            HttpResponse httpResponse = getRemote().download(attachable.getDownloadUri(getId()));
            in = httpResponse.getEntity().getContent();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return in;
    }

}
