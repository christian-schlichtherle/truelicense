<?xml version='1.0'?>
<!--
  ~ Copyright (C) 2005-2017 Schlichtherle IT Services.
  ~ All rights reserved. Use is subject to license terms.
  -->
<xsl:stylesheet
        exclude-result-prefixes="d xs"
        version="1.0"
        xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:d="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output indent="yes"/>

    <xsl:template match="/d:archetype-descriptor/d:requiredProperties">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:variable name="schema" select="document('../../archetype-properties.xsd')/xs:schema"/>
            <xsl:for-each select="$schema/xs:complexType[@name = 'List']/xs:all/xs:element">
                <requiredProperty key="{@name}">
                    <xsl:if test="@default">
                        <defaultValue>
                            <xsl:value-of select="@default"/>
                        </defaultValue>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when test="@name = 'editions'">
                            <validationRegex>([A-Za-z_$][A-Za-z0-9_$]*\s*)+</validationRegex>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="type" select="substring-after(@type, ':')"/>
                            <xsl:variable name="enumerationValues" select="$schema/xs:simpleType[@name = $type]/xs:restriction/xs:enumeration/@value"/>
                            <xsl:if test="$enumerationValues">
                                <validationRegex>
                                    <xsl:for-each select="$enumerationValues">
                                        <xsl:if test="position() != 1">
                                            <xsl:text>|</xsl:text>
                                        </xsl:if>
                                        <xsl:text>\Q</xsl:text>
                                        <xsl:value-of select="."/>
                                        <xsl:text>\E</xsl:text>
                                    </xsl:for-each>
                                </validationRegex>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </requiredProperty>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
