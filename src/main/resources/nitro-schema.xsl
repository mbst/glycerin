<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="xs:group">
        <xsl:choose>
            <xsl:when test="@ref = 'version_group' ">
                <xs:element ref="version"/>
            </xsl:when>
            <xsl:when test="@name = 'version_group'">
                <xs:element name="version">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element ref="pid"/>
                            <xs:element ref="ids" minOccurs="0"/>
                            <xs:element name="aspect_ratio" minOccurs="0" type="xs:string"/>
                            <xs:element ref="competition_warning" minOccurs="0"/>
                            <xs:element name="duration" minOccurs="0" type="xs:duration"/>
                            <xs:element ref="updated_time"/>
                            <xs:group ref="version_types_with_id" minOccurs="0"/>
                            <xs:group ref="types_with_id" minOccurs="0"/>
                            <xs:element ref="warnings" minOccurs="0"/>
                            <xs:element ref="version_of" minOccurs="0"/>
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
            </xsl:when>
            <xsl:when test="@name = 'people_mixin_contribution' ">
                <xs:group name="people_mixin_contribution">
                    <xs:sequence>
                        <xs:element name="contribution">
                            <xs:complexType>
                                <xs:sequence>
                                    <xs:group ref="people_mixin_contributor_name" minOccurs="0"/>
                                </xs:sequence>
                                <xs:attribute name="contribution_by" type="pid"/>
                                <xs:attribute name="credit_role_id" type="xs:string"/>
                                <xs:attribute name="credit_role" type="xs:string"/>
                                <xs:attribute name="character_name" type="xs:string"/>
                            </xs:complexType>
                        </xs:element>
                    </xs:sequence>
                </xs:group>
            </xsl:when>
            <xsl:when test="@name = 'contributions_mixin_contribution' ">
                <xs:group name="contributions_mixin_contribution">
                    <xs:sequence>
                        <xs:element name="contribution">
                            <xs:complexType>
                                <xs:sequence>
                                    <xs:element name="credit_role" minOccurs="0">
                                        <xs:complexType>
                                            <xs:simpleContent>
                                                <xs:extension base="xs:string">
                                                    <xs:attribute name="id" type="xs:string"/>
                                                </xs:extension>
                                            </xs:simpleContent>
                                        </xs:complexType>
                                    </xs:element>
                                    <xs:element name="character_name" type="xs:string" minOccurs="0"/>
                                    <xs:group ref="contributions_mixin_contributor" minOccurs="0"/>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:element>
                    </xs:sequence>
                </xs:group>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
