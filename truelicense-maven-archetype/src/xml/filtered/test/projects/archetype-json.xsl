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
        <xsl:variable name="definitions" select="document('../../main/archetype-properties.xsd')/xs:schema/xs:complexType[@name = 'List']/xs:all/xs:element"/>
        <xsl:text>{</xsl:text>
        <xsl:for-each select="p:list/*">
            <xsl:variable name="name" select="local-name()"/>
            <xsl:variable name="value" select="text()"/>
            <xsl:variable name="type" select="substring-after($definitions[@name = $name]/@type, ':')"/>
            <xsl:if test="position() != 1">
                <xsl:text>,</xsl:text>
            </xsl:if>
            <xsl:text>&lineSeparator;  "</xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>": </xsl:text>
            <xsl:choose>
                <xsl:when test="$type = 'boolean' or $type = 'nonNegativeInteger'">
                    <xsl:value-of select="$value"/>
                </xsl:when>
                <xsl:when test="$type = 'Password'">
                    <xsl:text>"</xsl:text>
                    <xsl:value-of select="$value"/>
                    <xsl:text>"</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>"</xsl:text>
                    <xsl:value-of select="normalize-space($value)"/>
                    <xsl:text>"</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>&lineSeparator;}</xsl:text>
    </xsl:template>
</xsl:stylesheet>
