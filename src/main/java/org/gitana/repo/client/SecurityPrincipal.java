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

package org.gitana.repo.client;

import org.gitana.repo.support.ResultMap;
import org.gitana.security.PrincipalType;

import java.security.Principal;
import java.util.List;

/**
 * @author uzi
 */
public interface SecurityPrincipal extends Document, Principal, Attachable, Selfable
{
    // default collection location
    public final static String DEFAULT_COLLECTION_ID = "principals";

    // fields
    public final static String FIELD_PRINCIPAL_ID = "principal-id";
    public final static String FIELD_PRINCIPAL_TYPE = "principal-type";

    // fields
    public final static String FIELD_AUTHORITIES = "authorities";

    public PrincipalType getPrincipalType();

    public void setPrincipalType(PrincipalType principalType);

    /**
     * @return granted authorities for this user
     */
    public List<String> getAuthorities();

    // PARENTS

    public ResultMap<SecurityGroup> fetchParentGroups();

    public ResultMap<SecurityGroup> fetchParentGroups(boolean includeAncestors);

    public List<SecurityGroup> listParentGroups();

    public List<SecurityGroup> listParentGroups(boolean includeAncestors);

}