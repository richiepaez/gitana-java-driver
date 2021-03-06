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

package org.gitana.platform.client.warehouse;

import org.gitana.platform.client.support.Selfable;

import java.util.List;

/**
 * @author uzi
 */
public interface InteractionPage extends WarehouseDocument, Selfable
{
    public final static String FIELD_URI = "uri";

    public final static String FIELD_INTERACTION_APPLICATION_ID = "interactionApplicationId";

    public final static String FIELD_SNAPSHOT = "snapshot";
    public final static String FIELD_SNAPSHOT_WIDTH = "width";
    public final static String FIELD_SNAPSHOT_HEIGHT = "height";

    public final static String FIELD_ELEMENTS = "elements";

    public final static String FIELD_ELEMENT_TYPE = "type";
    public final static String FIELD_ELEMENT_X = "x";
    public final static String FIELD_ELEMENT_Y = "y";
    public final static String FIELD_ELEMENT_WIDTH = "width";
    public final static String FIELD_ELEMENT_HEIGHT = "height";

    public String getUri();
    public void setUri(String uri);

    public String getInteractionApplicationId();
    public void setInteractionApplicationId(String interactionApplicationId);

    public int getSnapshotWidth();
    public void setSnapshotWidth(int snapshotWidth);

    public int getSnapshotHeight();
    public void setSnapshotHeight(int snapshotHeight);

    public List<String> getElementIIDs();

    public void addElement(String iid, String type, int x, int y, int width, int height);
}
