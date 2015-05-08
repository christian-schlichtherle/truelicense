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
        exclude-result-prefixes="d xs"
        version="1.0"
        xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:d="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/d:archetype-descriptor/d:requiredProperties">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:for-each
                    select="document('../../archetype-properties.xsd')/xs:schema/xs:complexType[@name = 'List']/xs:all/xs:element">
                <xsl:text>&lineSeparator;        </xsl:text>
                <requiredProperty key="{@name}">
                    <xsl:if test="@default">
                        <xsl:text>&lineSeparator;            </xsl:text>
                        <defaultValue>
                            <xsl:value-of select="@default"/>
                        </defaultValue>
                        <xsl:text>&lineSeparator;        </xsl:text>
                    </xsl:if>
                </requiredProperty>
            </xsl:for-each>
            <xsl:text>&lineSeparator;    </xsl:text>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
