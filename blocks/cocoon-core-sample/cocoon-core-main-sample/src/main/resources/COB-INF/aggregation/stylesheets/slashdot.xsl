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

<!--
  - $Id: slashdot.xsl 596918 2007-11-21 03:26:02Z vgritsenko $
  -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <samples name="Slashdot News Page">
      <group name="Slashdot News">
        <xsl:apply-templates select="backslash"/>
      </group>
    </samples>
  </xsl:template>

	<xsl:template match="backslash">
    <xsl:apply-templates select="story"/>
	</xsl:template>

  <xsl:template match="story">
    <sample name="{title}" href="{url}">
      <br/>Posted by <xsl:value-of select="author"/> at <xsl:value-of select="time"/>, <xsl:value-of select="comments"/> comments.
    </sample>
	</xsl:template>
</xsl:stylesheet>
