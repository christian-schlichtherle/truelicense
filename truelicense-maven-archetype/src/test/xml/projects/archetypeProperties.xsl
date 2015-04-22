<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2005-2015 Schlichtherle IT Services.
  ~ All rights reserved. Use is subject to license terms.
  -->
<!DOCTYPE stylesheet [
        <!-- Use whatever line separator is used in this document: CR, LF or CRLF. -->
        <!ENTITY lineSeparator "
">
        ]>
<xsl:stylesheet
        version="1.0"
        xmlns:ap="${project.url}/xml/archetypeProperties"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">

    <xsl:output method="text"/>

    <xsl:template match="/ap:archetypeProperties">
        <xsl:variable name="configuredProperties" select="*"/>
        <xsl:for-each select="$archetypeProperties">
            <xsl:variable name="propertyName" select="@name"/>
            <xsl:variable name="configuredValue"
                          select="$configuredProperties[name() = $propertyName]"/>
            <xsl:variable name="defaultValue" select="@default"/>
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
                <xsl:when test="$propertyName = 'package'">
                    <xsl:value-of
                            select="$configuredProperties[name() = 'groupId']"/>
                </xsl:when>
                <xsl:when test="$defaultValue">
                    <xsl:value-of select="$defaultValue"/>
                </xsl:when>
            </xsl:choose>
            <xsl:text>&lineSeparator;</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:variable name="archetypeProperties"
                  select="document('${meta-inf-maven-directory}/archetypeProperties.xsd')/xs:schema/xs:complexType[@name='ArchetypeProperties']/xs:all/xs:element"/>
</xsl:stylesheet>
