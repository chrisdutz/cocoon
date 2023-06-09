<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- SVN $Id: nowindow.xsl 451258 2006-09-29 12:13:39Z cziegeler $ -->
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="window">
    <xsl:variable name="copletId"><xsl:value-of select="instance-id"/></xsl:variable>
    <div class="coplet-nowindow" id="coplet-{$copletId}">
      <div class="coplet-content" id="coplet-content-{$copletId}">
        <xsl:apply-templates select="content"/>
      </div>
    </div>
  </xsl:template>

  <!-- This is the content of the coplet. We just remove the surrounding tag. -->
  <xsl:template match="content">
    <xsl:copy-of select="*"/>
  </xsl:template>

  <!-- Copy all and apply templates -->
  <xsl:template match="@*|node()">
    <xsl:copy><xsl:apply-templates select="@*|node()" /></xsl:copy>
  </xsl:template>

</xsl:stylesheet>
