/**
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.transformation;

import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.Slf4jBuildLogger;
import org.apache.commons.lang3.StringUtils;

public class ToQuotedNameTransformer extends NameTransformer {
    public ToQuotedNameTransformer() {
        super(new Slf4jBuildLogger(LOGGER));
    }

    public ToQuotedNameTransformer(BuildLogger logger) {
        super(logger);
    }

    @Override
    public String transformName(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        } else if (isSimpleName(name)) {
            return name;
        } else {
            return "'" + name + "'";
        }
    }

    private boolean isSimpleName(String name) {
        if (!isSimpleNameStart(name.codePointAt(0))) {
            return false;
        }
        for (int cp : name.codePoints().toArray()) {
            if (!(isSimpleNamePart(cp))) {
                return false;
            }
        }
        return true;
    }
}
