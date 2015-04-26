<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2005-2015 Schlichtherle IT Services.
  ~ All rights reserved. Use is subject to license terms.
  -->
<xsl:stylesheet
        exclude-result-prefixes="h p xs"
        version="1.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:h="http://www.w3.org/1999/xhtml"
        xmlns:p="${project.url}/xml/archetype-properties"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:key name="simpleType" match="/xs:schema/xs:simpleType" use="@name"/>

    <xsl:output omit-xml-declaration="yes"/>

    <xsl:template match="h:head">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="markdown"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="h:body">
        <xsl:apply-templates mode="markdown"/>
    </xsl:template>

    <xsl:template match="p:properties" mode="markdown">
        <xsl:text>    $ mvn archetype:generate -B \
        -DarchetypeGroupId=${project.groupId} \
        -DarchetypeArtifactId=${project.artifactId} \
        -DarchetypeVersion=${project.version}</xsl:text>
        <xsl:for-each select="*">
            <xsl:variable name="name" select="local-name()"/>
            <xsl:variable name="value" select="text()"/>
            <xsl:text> \
        -D</xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>='</xsl:text>
            <xsl:choose>
                <xsl:when test="$name = 'password'">
                    <xsl:value-of select="$value"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="normalize-space($value)"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>'</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="h:a[contains(@class, 'maven-command')]"
                  mode="markdown">
        <xsl:apply-templates select="document(@href)/p:properties"
                             mode="markdown"/>
    </xsl:template>

    <xsl:template match="h:div[contains(@class, 'property-reference')]"
                  mode="markdown">
        <xsl:param name="lang">
            <xsl:call-template name="lang"/>
        </xsl:param>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <div class="accordion" id="accordion2">
                <xsl:for-each
                        select="document('archetype-properties.xsd')/xs:schema/xs:complexType[@name = 'Properties']/xs:all/xs:element">
                    <xsl:variable name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:variable>
                    <xsl:variable name="type">
                        <xsl:call-template name="type">
                            <xsl:with-param name="lang" select="$lang"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="default">
                        <xsl:value-of select="@default"/>
                    </xsl:variable>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle"
                               href="{ concat('#collapse', position()) }">
                                <xsl:variable name="declaration">
                                    <xsl:copy-of select="$name"/>
                                    <xsl:text>: </xsl:text>
                                    <xsl:copy-of select="$type//h:abbr"/>
                                </xsl:variable>
                                <xsl:choose>
                                    <xsl:when test="@default">
                                        <xsl:copy-of select="$declaration"/>
                                        <xsl:text> [</xsl:text>
                                        <code>
                                            <xsl:copy-of select="$default"/>
                                        </code>
                                        <xsl:text>]</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <strong>
                                            <xsl:copy-of select="$declaration"/>
                                        </strong>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </a>
                        </div>
                        <xsl:variable name="optionalIn">
                            <xsl:if test="not(@default)">
                                <xsl:text> in</xsl:text>
                            </xsl:if>
                        </xsl:variable>
                        <div id="{ concat('collapse', position()) }"
                             class="accordion-body collapse{ $optionalIn }">
                            <div class="accordion-inner">
                                <dl class="dl-horizontal">
                                    <xsl:for-each
                                            select="xs:annotation/xs:documentation[lang($lang)]">
                                        <dt>Description</dt>
                                        <dd>
                                            <xsl:apply-templates select="node()"
                                                                 mode="stripped"/>
                                        </dd>
                                    </xsl:for-each>
                                    <dt>Type</dt>
                                    <dd>
                                        <xsl:copy-of select="$type"/>
                                    </dd>
                                    <xsl:if test="@default">
                                        <dt>Default</dt>
                                        <dd>
                                            <code>
                                                <xsl:copy-of select="$default"/>
                                            </code>
                                        </dd>
                                    </xsl:if>
                                </dl>
                            </div>
                        </div>
                    </div>
                </xsl:for-each>
            </div>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="type">
        <xsl:param name="lang">
            <xsl:call-template name="lang"/>
        </xsl:param>
        <xsl:variable name="localName" select="substring-after(@type, ':')"/>
        <xsl:variable name="nsPrefix" select="substring-before(@type, ':')"/>
        <xsl:variable name="nsUri"
                      select="namespace::*[local-name() = $nsPrefix]"/>
        <xsl:choose>
            <xsl:when test="$nsUri = 'http://www.w3.org/2001/XMLSchema'">
                <a href="http://www.w3.org/TR/xmlschema-2/#{$localName}">
                    <abbr title="A built-in datatype of XML Schema.">
                        <xsl:value-of select="$localName"/>
                    </abbr>
                </a>
            </xsl:when>
            <xsl:when test="$nsUri = '${project.url}/xml/archetype-properties'">
                <xsl:for-each select="key('simpleType', $localName)">
                    <xsl:variable name="href" select="@h:href"/>
                    <xsl:variable name="output">
                        <abbr title="A custom datatype based on a built-in datatype of XML Schema with additional restrictions.">
                            <xsl:value-of select="$localName"/>
                        </abbr>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$href">
                            <a href="{$href}">
                                <xsl:copy-of select="$output"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$output"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>: </xsl:text>
                    <xsl:variable
                            name="enumerations"
                            select="xs:restriction/xs:enumeration"/>
                    <xsl:if test="$enumerations">
                        <xsl:text>One of </xsl:text>
                        <xsl:for-each select="$enumerations">
                            <xsl:choose>
                                <xsl:when test="position() = last()">
                                    <xsl:text> or </xsl:text>
                                </xsl:when>
                                <xsl:when test="position() != 1">
                                    <xsl:text>, </xsl:text>
                                </xsl:when>
                            </xsl:choose>
                            <code>
                                <xsl:value-of select="@value"/>
                            </code>
                        </xsl:for-each>
                        <xsl:text>. </xsl:text>
                    </xsl:if>
                    <xsl:apply-templates
                            select="xs:annotation/xs:documentation[lang($lang)]/node()"
                            mode="stripped"/>
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="lang">
        <xsl:variable name="lang"
                      select="ancestor-or-self::*[@xml:lang][1]/@xml:lang"/>
        <xsl:choose>
            <xsl:when test="$lang">
                <xsl:value-of select="$lang"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>en</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@* | node()" mode="markdown">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" mode="markdown"/>
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
</xsl:stylesheet>
