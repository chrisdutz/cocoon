<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:n="http://www.betaversion.org/linotype/news/1.0">

  <xsl:param name="home"/>

  <xsl:template match="/">
   <html>
    <head>
     <title>News List</title>
     <link rel="stylesheet" type="text/css" href="{$home}/styles/main.css"/>
    </head>
    <body>

     <div id="page">
      <h1>News List</h1>
      <h2>where the juice is</h2>
      <h3><a href="edit/news/template/">Write a news</a><span class="separator">|</span><a href="logout">Logout</a></h3>
      <xsl:apply-templates select="//n:news"/>
     </div>

     <div id="sidebar">
      <a href="{$home}/"><img alt="Linotype" src="{$home}/images/linotype.jpg" width="156px" height="207px" /></a>
     </div>

     <div id="bottombar">
      <a href="http://cocoon.apache.org" title="Apache Cocoon"><img alt="Powered by Cocoon" src="{$home}/images/cocoon.jpg"/></a>
     </div>

    </body>
   </html>
  </xsl:template>

  <xsl:template match="n:news">
   <xsl:variable name="id" select="../@id"/>
   <div>
    <xsl:choose>
     <xsl:when test="starts-with($id,'template-')">
      <xsl:attribute name="class">news template</xsl:attribute>
     </xsl:when>
     <xsl:otherwise>
      <xsl:attribute name="class">news</xsl:attribute>
     </xsl:otherwise>
    </xsl:choose>
    <h1><img src="{$home}/images/hand.jpg" alt=""/><a href="edit/news/{$id}/"><xsl:value-of select="n:title"/></a></h1>
    <h2><xsl:value-of select="@creation-date"/> ~ <xsl:value-of select="@creation-time"/></h2>
   </div>
  </xsl:template>

</xsl:stylesheet>
