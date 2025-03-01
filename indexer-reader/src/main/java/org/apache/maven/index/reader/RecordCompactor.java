package org.apache.maven.index.reader;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.index.reader.Record.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.apache.maven.index.reader.Utils.FIELD_SEPARATOR;
import static org.apache.maven.index.reader.Utils.INFO;
import static org.apache.maven.index.reader.Utils.UINFO;
import static org.apache.maven.index.reader.Utils.nvl;

/**
 * Maven Index record transformer, that transforms {@link Record}s into "native" Maven Indexer records.
 *
 * @since 5.1.2
 */
public class RecordCompactor
    implements Function<Record, Map<String, String>>
{
    /**
     * Compacts {@link Record} into low level MI record with all the encoded fields as physically present in MI binary
     * chunk.
     */
    @Override
    public Map<String, String> apply( final Record record )
    {
        if ( Type.DESCRIPTOR == record.getType() )
        {
            return compactDescriptor( record );
        }
        else if ( Type.ALL_GROUPS == record.getType() )
        {
            return compactAllGroups( record );
        }
        else if ( Type.ROOT_GROUPS == record.getType() )
        {
            return compactRootGroups( record );
        }
        else if ( Type.ARTIFACT_REMOVE == record.getType() )
        {
            return compactDeletedArtifact( record );
        }
        else if ( Type.ARTIFACT_ADD == record.getType() )
        {
            return compactAddedArtifact( record );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown record: " + record );
        }
    }

    private static Map<String, String> compactDescriptor( final Record record )
    {
        final Map<String, String> result = new HashMap<>();
        result.put( "DESCRIPTOR", "NexusIndex" );
        result.put( "IDXINFO", "1.0|" + record.getString( Record.REPOSITORY_ID ) );
        return result;
    }

    private static Map<String, String> compactAllGroups( final Record record )
    {
        final Map<String, String> result = new HashMap<>();
        result.put( "allGroups", "allGroups" );
        putIfNotNullAsStringArray( record.getStringArray( Record.ALL_GROUPS ), result, "allGroupsList" );
        return result;
    }

    private static Map<String, String> compactRootGroups( final Record record )
    {
        final Map<String, String> result = new HashMap<>();
        result.put( "rootGroups", "allGroups" );
        putIfNotNullAsStringArray( record.getStringArray( Record.ROOT_GROUPS ), result, "rootGroupsList" );
        return result;
    }

    private static Map<String, String> compactDeletedArtifact( final Record record )
    {
        final Map<String, String> result = new HashMap<>();
        putIfNotNullTS( record.getLong( Record.REC_MODIFIED ), result, "m" );
        result.put( "del", compactUinfo( record ) );
        return result;
    }

    /**
     * Expands the "encoded" Maven Indexer record by splitting the synthetic fields and applying expanded field naming.
     */
    private static Map<String, String> compactAddedArtifact( final Record record )
    {
        final Map<String, String> result = new HashMap<>();

        // Minimal
        result.put( UINFO, compactUinfo( record ) );

        String info = nvl( record.getString( Record.PACKAGING ) ) + FIELD_SEPARATOR
                + record.getLong( Record.FILE_MODIFIED ) + FIELD_SEPARATOR
                + record.getLong( Record.FILE_SIZE ) + FIELD_SEPARATOR
                + ( record.getBoolean( Record.HAS_SOURCES ) ? "1" : "0" ) + FIELD_SEPARATOR
                + ( record.getBoolean( Record.HAS_JAVADOC ) ? "1" : "0" ) + FIELD_SEPARATOR
                + ( record.getBoolean( Record.HAS_SIGNATURE ) ? "1" : "0" ) + FIELD_SEPARATOR
                + nvl( record.getString( Record.FILE_EXTENSION ) );
        result.put( INFO, info );

        putIfNotNullTS( record.getLong( Record.REC_MODIFIED ), result, "m" );
        putIfNotNull( record.getString( Record.NAME ), result, "n" );
        putIfNotNull( record.getString( Record.DESCRIPTION ), result, "d" );
        putIfNotNull( record.getString( Record.SHA1 ), result, "1" );

        // Jar file contents (optional)
        putIfNotNullAsStringArray( record.getStringArray( Record.CLASSNAMES ), result, "classnames" );

        // Maven Plugin (optional)
        putIfNotNull( record.getString( Record.PLUGIN_PREFIX ), result, "px" );
        putIfNotNullAsStringArray( record.getStringArray( Record.PLUGIN_GOALS ), result, "gx" );

        // OSGi (optional)
        putIfNotNull( record.getString( Record.OSGI_BUNDLE_SYMBOLIC_NAME ), result, "Bundle-SymbolicName" );
        putIfNotNull( record.getString( Record.OSGI_BUNDLE_VERSION ), result, "Bundle-Version" );
        putIfNotNull( record.getString( Record.OSGI_EXPORT_PACKAGE ), result, "Export-Package" );
        putIfNotNull( record.getString( Record.OSGI_EXPORT_SERVICE ), result, "Export-Service" );
        putIfNotNull( record.getString( Record.OSGI_BUNDLE_DESCRIPTION ), result, "Bundle-Description" );
        putIfNotNull( record.getString( Record.OSGI_BUNDLE_NAME ), result, "Bundle-Name" );
        putIfNotNull( record.getString( Record.OSGI_BUNDLE_LICENSE ), result, "Bundle-License" );
        putIfNotNull( record.getString( Record.OSGI_EXPORT_DOCURL ), result, "Bundle-DocURL" );
        putIfNotNull( record.getString( Record.OSGI_IMPORT_PACKAGE ), result, "Import-Package" );
        putIfNotNull( record.getString( Record.OSGI_REQUIRE_BUNDLE ), result, "Require-Bundle" );
        putIfNotNull( record.getString( Record.OSGI_PROVIDE_CAPABILITY ), result, "Provide-Capability" );
        putIfNotNull( record.getString( Record.OSGI_REQUIRE_CAPABILITY ), result, "Require-Capability" );
        putIfNotNull( record.getString( Record.OSGI_FRAGMENT_HOST ), result, "Fragment-Host" );
        putIfNotNull( record.getString( Record.OSGI_BREE ), result, "Bundle-RequiredExecutionEnvironment" );
        putIfNotNull( record.getString( Record.SHA_256 ), result, "sha256" );

        return result;
    }

    /**
     * Creates UINFO synthetic field.
     */
    private static String compactUinfo( final Record record )
    {
        final String classifier = record.getString( Record.CLASSIFIER );
        StringBuilder sb = new StringBuilder().append( record.getString( Record.GROUP_ID ) ).append( FIELD_SEPARATOR )
                .append( record.getString( Record.ARTIFACT_ID ) ).append( FIELD_SEPARATOR )
                .append( record.getString( Record.VERSION ) ).append( FIELD_SEPARATOR ).append( nvl( classifier ) );
        if ( classifier != null )
        {
            sb.append( FIELD_SEPARATOR ).append( record.get( Record.FILE_EXTENSION ) );
        }
        return sb.toString();
    }

    /**
     * Helper to put a value from source map into target map, if not null.
     */
    private static void putIfNotNull( final String source, final Map<String, String> target, final String targetName )
    {
        if ( source != null )
        {
            target.put( targetName, source );
        }
    }

    /**
     * Helper to put a {@link Long} value from source map into target map, if not null.
     */
    private static void putIfNotNullTS( final Long source, final Map<String, String> target, final String targetName )
    {
        if ( source != null )
        {
            target.put( targetName, String.valueOf( source ) );
        }
    }

    /**
     * Helper to put a array value from source map into target map joined with {@link Utils#FIELD_SEPARATOR}, if not
     * null.
     */
    private static void putIfNotNullAsStringArray( final String[] source, final Map<String, String> target,
                                                   final String targetName )
    {
        if ( source != null && source.length > 0 )
        {
            StringBuilder sb = new StringBuilder();
            sb.append( source[0] );
            for ( int i = 1; i < source.length; i++ )
            {
                sb.append( FIELD_SEPARATOR ).append( source[i] );
            }
            target.put( targetName, sb.toString() );
        }
    }
}
