<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2005-2015 Schlichtherle IT Services.
  ~ All rights reserved. Use is subject to license terms.
  -->
<!DOCTYPE stylesheet [
        <!ENTITY lf "&#10;">
        ]>
<xsl:stylesheet version="1.0"
                xmlns:ad="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
                xmlns:ap="${project.url}/xml/archetypeProperties"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xsi:schemaLocation="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0 http://maven.apache.org/xsd/archetype-descriptor-1.0.0.xsd
                                    http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">
    <xsl:variable name="allProperties"
                  select="document('${project.build.outputDirectory}/META-INF/maven/archetypeProperties.xsd')/xs:schema/xs:complexType[@name='ArchetypeProperties']/xs:all/xs:element"/>
    <xsl:variable name="requiredProperties"
                  select="document('${project.build.outputDirectory}/META-INF/maven/archetype-metadata.xml')/ad:archetype-descriptor/ad:requiredProperties/ad:requiredProperty"/>

    <xsl:template match="/ap:archetypeProperties">
        <xsl:variable name="configuredProperties" select="*"/>
        <xsl:for-each select="$allProperties">
            <xsl:variable name="propertyName" select="@name"/>
            <xsl:variable name="configuredValue"
                          select="$configuredProperties[name() = $propertyName]"/>
            <xsl:variable name="defaultValue"
                          select="$requiredProperties[@key = $propertyName]/ad:defaultValue"/>
            <xsl:value-of select="$propertyName"/>
            <xsl:text>=</xsl:text>
            <xsl:choose>
                <xsl:when test="$configuredValue">
                    <xsl:choose>
                        <xsl:when
                                test="substring-after(@type, ':') = 'Password'">
                            <xsl:value-of select="$configuredValue"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of
                                    select="normalize-space($configuredValue)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$defaultValue">
                    <xsl:value-of select="$defaultValue"/>
                </xsl:when>
                <xsl:when test="$propertyName = 'package'">
                    <xsl:value-of
                            select="$configuredProperties[name() = 'groupId']"/>
                </xsl:when>
            </xsl:choose>
            <xsl:text>&lf;</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:output method="text"/>
</xsl:stylesheet>
