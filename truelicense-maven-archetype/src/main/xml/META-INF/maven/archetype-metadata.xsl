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
        exclude-result-prefixes="ad xs"
        version="1.0"
        xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:ad="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0 http://maven.apache.org/xsd/archetype-descriptor-1.0.0.xsd
                            http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">

    <xsl:template match="/ad:archetype-descriptor/ad:requiredProperties">
        <requiredProperties>
            <xsl:for-each select="$archetypeProperties">
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
        </requiredProperties>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:variable name="archetypeProperties"
                  select="document('archetypeProperties.xsd')/xs:schema/xs:complexType[@name='ArchetypeProperties']/xs:all/xs:element"/>
</xsl:stylesheet>
