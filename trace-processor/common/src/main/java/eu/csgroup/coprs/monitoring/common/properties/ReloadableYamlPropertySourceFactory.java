/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.monitoring.common.properties;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

/**
 * Specialized factory to use an alternative way to load YAML configuration file and data sent to spring boot to create
 * associated configuration object
 */
public class ReloadableYamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource)
            throws IOException {

        Resource internal = encodedResource.getResource();

        if (internal instanceof FileSystemResource fileSystemResource)
            return new ReloadableYamlPropertySource(s, fileSystemResource.getPath());
        if (internal instanceof FileUrlResource fileUrlResource)
            return new ReloadableYamlPropertySource(s, fileUrlResource.getURL().getPath());
        if (internal instanceof ClassPathResource)
            return new ReloadableYamlPropertySource(s, internal
                    .getFile()
                    .getAbsolutePath());

        return super.createPropertySource(s, encodedResource);
    }
}