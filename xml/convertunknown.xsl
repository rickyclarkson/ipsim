<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="/object">
        <xsl:copy>
   	        <xsl:apply-templates select="@*"/>
   	        <attribute name="version" value="1.4"/>
	        <xsl:apply-templates select="node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="object">
		<xsl:if test="@serialiser='ipsim.persistence.delegates.ComputerDelegate'">
			<xsl:copy>
				<xsl:apply-templates select="@*"/>
				<attribute name="computerId" value="{@id}"/>
				<xsl:apply-templates select="node()"/>
			</xsl:copy>
		</xsl:if>

		<xsl:if test="not(@serialiser='ipsim.persistence.delegates.ComputerDelegate')">
			<xsl:copy>
				<xsl:apply-templates select="node()|@*"/>
			</xsl:copy>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>