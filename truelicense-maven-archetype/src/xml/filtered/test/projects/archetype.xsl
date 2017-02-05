<?xml version='1.0'?>
<!--
  ~ Copyright (C) 2005-2017 Schlichtherle IT Services.
  ~ All rights reserved. Use is subject to license terms.
  -->
<!DOCTYPE stylesheet [
        <!-- Use whatever line separator is used in this document: CR, LF or CRLF. -->
        <!ENTITY lineSeparator "
">
        ]>
<xsl:stylesheet
        version="1.0"
        xmlns:p="${project.url}/xml/archetype-properties"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">

    <xsl:output method="text"/>

    <xsl:template match="/p:properties">
        <xsl:variable name="properties" select="p:list/*"/>
        <xsl:for-each select="document('../../main/archetype-properties.xsd')/xs:schema/xs:complexType[@name = 'List']/xs:all/xs:element">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="value" select="$properties[local-name() = $name]"/>
            <xsl:variable name="default" select="@default"/>
            <xsl:value-of select="$name"/>
            <xsl:text>=</xsl:text>
            <xsl:choose>
                <xsl:when test="$value">
                    <xsl:choose>
                        <xsl:when test="substring-after(@type, ':') = 'Password'">
                            <xsl:value-of select="$value"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="normalize-space($value)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$name = 'package'">
                    <xsl:value-of select="$properties[local-name() = 'groupId']"/>
                </xsl:when>
                <xsl:when test="$default">
                    <xsl:value-of select="$default"/>
                </xsl:when>
            </xsl:choose>
            <xsl:text>&lineSeparator;</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
