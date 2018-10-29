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
        exclude-result-prefixes="pom xs"
        version="1.0"
        xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:pom="http://maven.apache.org/POM/4.0.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd
                            http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">

    <xsl:template match="/pom:project/pom:properties/*[@prefix]">
        <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
        <xsl:value-of select="@prefix"/>
        <xsl:value-of select="name()"/>
        <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
        <xsl:apply-templates select="node()"/>
        <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
        <xsl:value-of select="@prefix"/>
        <xsl:value-of select="name()"/>
        <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    </xsl:template>

    <xsl:template match="/pom:project/pom:properties">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="@* | node()"/>
            <xsl:for-each select="document('../archetype-properties.xsd')/xs:schema/xs:complexType[@name = 'List']/xs:all/xs:element">
                <xsl:text>    </xsl:text>
                <xsl:element name="{@name}">
                    <xsl:text>$</xsl:text>
                    <xsl:value-of select="@name"/>
                </xsl:element>
                <xsl:text>&lineSeparator;    </xsl:text>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
