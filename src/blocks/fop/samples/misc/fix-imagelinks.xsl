<?xml version="1.0" encoding="utf-8"?>

<!-- A small stylesheet to make image links in *.fo files absolute.  This is
currently necessary because FOP doesn't know the 'context' in which it is
operating, and so cannot resolve relative URLs.  This hack will go away once the
Cocoon<->FOP link improves (it's fixed for CVS trunk FOP, but that isn't too
usable yet).  -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <!-- Absolute path to context root -->
  <xsl:param name="ctxroot" select="'[ctxroot]'"/>

  <!-- context-relative path of current directory -->
  <xsl:param name="dir" select="'[dir]'"/>

  <xsl:template match="fo:external-graphic/@src">
    <xsl:attribute name="src">
      <xsl:value-of select="$ctxroot"/>
      <xsl:choose>
        <xsl:when test="starts-with(., '/')">
          <xsl:value-of select="substring(., 2)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$dir"/>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>


  <!-- Copy across everything else unchanged. -->
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
