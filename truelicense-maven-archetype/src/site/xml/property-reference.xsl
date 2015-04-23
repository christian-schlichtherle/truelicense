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
        exclude-result-prefixes="h xdoc xs"
        version="1.0"
        xmlns="http://maven.apache.org/XDOC/2.0"
        xmlns:h="http://www.w3.org/1999/xhtml"
        xmlns:xdoc="http://maven.apache.org/XDOC/2.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xsi:schemaLocation="http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd
                            http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema.xsd">

    <xsl:param name="lang" select="'en'"/>

    <xsl:template match="xdoc:tbody[@id = 'properties']">
        <tbody>
            <xsl:for-each select="$schema/xs:complexType[@name='Properties']/xs:all/xs:element">
                <tr>
                    <td>
                        <code>
                            <xsl:value-of select="@name"/>
                        </code>
                    </td>
                    <td>
                        <dl class="dl-horizontal">
                            <dt>Description</dt>
                            <dd>
                                <xsl:apply-templates select="xs:annotation/xs:documentation[ancestor-or-self::*[@xml:lang][1]/@xml:lang = $lang]/node()"
                                                     mode="stripped"/>
                            </dd>
                            <dt>Type</dt>
                            <dd>
                                <xsl:variable name="localName"
                                              select="substring-after(@type, ':')"/>
                                <xsl:variable name="nsPrefix"
                                              select="substring-before(@type, ':')"/>
                                <xsl:variable name="nsUri"
                                              select="namespace::*[local-name() = $nsPrefix]"/>
                                <xsl:choose>
                                    <xsl:when
                                            test="$nsUri = 'http://www.w3.org/2001/XMLSchema'">
                                        <a href="http://www.w3.org/TR/xmlschema-2/#{$localName}">
                                            <abbr title="A built-in datatype of XML Schema.">
                                                <xsl:value-of
                                                        select="$localName"/>
                                            </abbr>
                                        </a>
                                    </xsl:when>
                                    <xsl:when
                                            test="$nsUri = '${project.url}/xml/archetype-properties'">
                                        <xsl:for-each select="$schema/xs:simpleType[@name = $localName]">
                                            <xsl:variable name="href"
                                                          select="@h:href"/>
                                            <xsl:variable name="typeOutput">
                                                <abbr title="A custom datatype based on a built-in datatype of XML Schema with additional restrictions.">
                                                    <xsl:value-of
                                                            select="$localName"/>
                                                </abbr>
                                            </xsl:variable>
                                            <xsl:choose>
                                                <xsl:when test="$href">
                                                    <a href="{$href}">
                                                        <xsl:copy-of select="$typeOutput"/>
                                                    </a>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:copy-of select="$typeOutput"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:text>: </xsl:text>
                                            <xsl:variable
                                                    name="enumerations"
                                                    select="xs:restriction/xs:enumeration"/>
                                            <xsl:if test="$enumerations">
                                                <xsl:text>One of </xsl:text>
                                                <xsl:for-each
                                                        select="$enumerations">
                                                    <xsl:choose>
                                                        <xsl:when
                                                                test="position() = last()">
                                                            <xsl:text> or </xsl:text>
                                                        </xsl:when>
                                                        <xsl:when
                                                                test="position() != 1">
                                                            <xsl:text>, </xsl:text>
                                                        </xsl:when>
                                                    </xsl:choose>
                                                    <code>
                                                        <xsl:value-of
                                                                select="@value"/>
                                                    </code>
                                                </xsl:for-each>
                                                <xsl:text>. </xsl:text>
                                            </xsl:if>
                                            <xsl:apply-templates
                                                    select="xs:annotation/xs:documentation[ancestor-or-self::*[@xml:lang][1]/@xml:lang = $lang]/node()"
                                                    mode="stripped"/>
                                        </xsl:for-each>
                                    </xsl:when>
                                </xsl:choose>
                            </dd>
                            <xsl:for-each select="@default">
                                <dt>Default</dt>
                                <dd>
                                    <code>
                                        <xsl:value-of select="."/>
                                    </code>
                                </dd>
                            </xsl:for-each>
                        </dl>
                    </td>
                </tr>
            </xsl:for-each>
        </tbody>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*" mode="stripped">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="@* | node()" mode="stripped"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@* | text()" mode="stripped">
        <xsl:copy/>
    </xsl:template>

    <xsl:variable name="schema"
                  select="document('archetype-properties.xsd')/xs:schema"/>
</xsl:stylesheet>
