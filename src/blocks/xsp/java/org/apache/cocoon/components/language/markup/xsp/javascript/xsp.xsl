<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- CVS $Id: xsp.xsl,v 1.3 2004/04/02 21:32:34 joerg Exp $-->
<!--
 * XSP Core logicsheet for the JavaScript language
 *
 * @author <a href="mailto:vgritsenko@apache.org>Vadim Gritsenko</a>
-->

<xsl:stylesheet version="1.0"
                xmlns:xsp="http://apache.org/xsp"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:XSLTExtension="org.apache.cocoon.components.language.markup.xsp.XSLTExtension">
  <xsl:output method="text"/>

  <xsl:variable name="xsp-uri" select="'http://apache.org/xsp'"/>

  <!--
    this variable holds the instance of extension class to properly
    escape text into Java strings

    ovidiu: use the class name as the namespace to identify the
    class. This is supposedly portable across XSLT implementations.
  -->
  <xsl:variable
       name="extension"
       select="XSLTExtension:new()"
       xmlns:XSLTExtension="org.apache.cocoon.components.language.markup.xsp.XSLTExtension"/>

  <xsl:template match="/">
    <code>
      <xsl:text>// $Cocoon extends: org.apache.cocoon.components.language.markup.xsp.JSGenerator$
</xsl:text>
      <xsl:for-each select="//xsp:dependency">
        <xsl:text>// $Cocoon depends: </xsl:text>
        <xsl:value-of select="translate(., '\','/')"/>
        <xsl:text>$
</xsl:text>
       </xsl:for-each>

      <xsl:apply-templates select="xsp:page"/>
    </code>
  </xsl:template>

  <xsl:template match="xsp:page" xml:space="preserve">
    // Generated by XSP. Edit at your own risk, :-)

    /* Built-in beans not available for use */
    // page           - "this" in Java
    // logger         - Logger
    // contentHandler - page.contentHandler
    // xspAttr        - AttributesImpl

    /* Built-in parameters available for use */
    // context    - org.apache.cocoon.environment.Context
    // request    - org.apache.cocoon.environment.Request
    // response   - org.apache.cocoon.environment.Response
    // parameters - parameters defined in the sitemap
    // objectModel- java.util.Map
    // resolver   - org.apache.cocoon.environment.SourceResolver

    // User Class "Declarations"
    <xsl:apply-templates select="xsp:logic"/>

    // User Class Initialization
    <xsl:value-of select="xsp:init-page"/>

    contentHandler.startDocument();

    <!-- Generate top-level processing instructions -->
    <xsl:apply-templates select="/processing-instruction()"/>

    <!-- Process only 1st non-XSP element as generated root -->
    <xsl:call-template name="process-first-element">
      <xsl:with-param name="content" select="*[not(namespace-uri(.) = $xsp-uri)][1]"/>
    </xsl:call-template>

    contentHandler.endDocument();
  </xsl:template>


  <xsl:template name="process-first-element">
    <xsl:param name="content"/>

    <!-- Generate top-level namespaces declarations -->
    <xsl:variable name="parent-element" select="$content/.."/>
    <xsl:for-each select="$content/namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <!-- Declare namespaces that also exist on the parent (i.e. not locally declared),
           and filter out "xmlns:xmlns" namespace produced by Xerces+Saxon -->
      <xsl:if test="($ns-prefix != 'xmlns') and $parent-element/namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri]">
        contentHandler.startPrefixMapping("<xsl:value-of select="$ns-prefix"/>", "<xsl:value-of select="$ns-uri"/>");
      </xsl:if>
    </xsl:for-each>

    <!-- Generate content -->
    <xsl:apply-templates select="$content"/>

    <!-- Close top-level namespaces declarations-->
    <xsl:for-each select="$content/namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <xsl:if test="($ns-prefix != 'xmlns') and $parent-element/namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri]">
        contentHandler.endPrefixMapping("<xsl:value-of select="local-name(.)"/>");
      </xsl:if>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="xsp:element">
    <xsl:variable name="uri">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">uri</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="prefix">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">prefix</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="@name and contains(@name, ':')">
      <xsl:call-template name="error">
        <xsl:with-param name="message">[&lt;xsp:element name="<xsl:value-of select="@name"/>"&gt;]
Name can not contain ':'. If you want to create namespaced element, specify 'uri' and 'prefix'.
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

    <xsl:variable name="name">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">name</xsl:with-param>
        <xsl:with-param name="required">true</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="raw-name">
      <xsl:if test="
        ($uri = '&quot;&quot;' and $prefix != '&quot;&quot;') or
        ($uri != '&quot;&quot;' and $prefix = '&quot;&quot;')
      ">
        <xsl:call-template name="error">
          <xsl:with-param name="message">[&lt;xsp:element&gt;]
Either both 'uri' and 'prefix' or none of them must be specified
          </xsl:with-param>
        </xsl:call-template>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="$prefix = '&quot;&quot;'">
          <xsl:copy-of select="$name"/>
        </xsl:when>
        <xsl:otherwise>
          (<xsl:copy-of select="$prefix"/>)+":"+(<xsl:copy-of select="$name"/>)
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:for-each select="namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <xsl:if test="not(../namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri])">
        contentHandler.startPrefixMapping("<xsl:value-of select="local-name(.)"/>", "<xsl:value-of select="."/>");
      </xsl:if>
    </xsl:for-each>

    <!-- Declare namespace defined by @uri and @prefix attribute -->
    <xsl:if test="$uri != '&quot;&quot;'">
      <xsl:if test="not(../namespace::*[local-name(.) = $prefix and string(.) = $uri])">
        contentHandler.startPrefixMapping(<xsl:value-of select="$prefix"/>, <xsl:value-of select="$uri"/>);
      </xsl:if>
    </xsl:if>

    <xsl:apply-templates select="xsp:attribute | xsp:logic[xsp:attribute]" mode="check"/>

    contentHandler.startElement(<xsl:copy-of select="$uri"/>,
      <xsl:copy-of select="$name"/>, <xsl:copy-of select="$raw-name"/>, xspAttr);
    xspAttr.clear();

    <xsl:apply-templates select="node()[not(
      (namespace-uri(.) = $xsp-uri and local-name(.) = 'attribute') or
      (namespace-uri(.) = $xsp-uri and local-name(.) = 'logic' and ./xsp:attribute)
      )]"/>

    contentHandler.endElement(<xsl:copy-of select="$uri"/>,
      <xsl:copy-of select="$name"/>, <xsl:copy-of select="$raw-name"/>);

    <!-- Declare namespace defined by @uri and @prefix attribute -->
    <xsl:if test="$uri != '&quot;&quot;'">
      <xsl:if test="not(../namespace::*[local-name(.) = $prefix and string(.) = $uri])">
        contentHandler.endPrefixMapping(<xsl:value-of select="$prefix"/>);
      </xsl:if>
    </xsl:if>

    <xsl:for-each select="namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <xsl:if test="not(../namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri])">
        contentHandler.endPrefixMapping("<xsl:value-of select="local-name(.)"/>");
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="xsp:attribute | xsp:logic[xsp:attribute]" mode="check">
    <xsl:if test="preceding-sibling::text()[normalize-space()]
                | preceding-sibling::processing-instruction()
                | preceding-sibling::*[not(self::xsp:attribute
                                         | self::xsp:logic[xsp:attribute]
                                         | self::xsp:param
                                         | self::xsp:text[not(normalize-space())])]">
      <xsl:message terminate="yes">
        You can not add attributes to elements after other node types already have been added.
      </xsl:message>
    </xsl:if>
    <xsl:apply-templates select="."/>
  </xsl:template>

  <xsl:template match="xsp:attribute">
    <xsl:variable name="uri">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">uri</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="prefix">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">prefix</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="name">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">name</xsl:with-param>
        <xsl:with-param name="required">true</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="raw-name">
      <xsl:if test="
        ($uri = '&quot;&quot;' and $prefix != '&quot;&quot;') or
        ($uri != '&quot;&quot;' and $prefix = '&quot;&quot;')
      ">
        <xsl:call-template name="error">
          <xsl:with-param name="message">[&lt;xsp:attribute&gt;]
Either both 'uri' and 'prefix' or none of them must be specified
          </xsl:with-param>
        </xsl:call-template>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="$prefix = '&quot;&quot;'">
          <xsl:copy-of select="$name"/>
        </xsl:when>
        <xsl:otherwise>
        (<xsl:copy-of select="$prefix"/> + ":" + <xsl:copy-of select="$name"/>)  </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="content">
      <xsl:for-each select="text()|xsp:expr|xsp:text">
        <xsl:if test="position() &gt; 1">
         +
        </xsl:if>
        <xsl:choose>
          <xsl:when test="namespace-uri(.) = $xsp-uri and local-name(.) = 'expr'">
            (<xsl:value-of select="."/>)
          </xsl:when>
          <xsl:otherwise>
            "<xsl:value-of select="XSLTExtension:escape($extension,.)"/>"
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:if test="not(text()|xsp:expr|xsp:text)">
        ""
      </xsl:if>
    </xsl:variable>

    xspAttr.addAttribute(<xsl:copy-of select="$uri"/>,
      <xsl:copy-of select="$name"/>, <xsl:copy-of select="$raw-name"/>, "CDATA",
      <xsl:value-of select="normalize-space($content)"/>);
  </xsl:template>


  <xsl:template match="xsp:expr">
    <xsl:choose>
      <xsl:when test="namespace-uri(..) = $xsp-uri and local-name(..) != 'content' and local-name(..) != 'element'">
        <!--
             Expression is nested inside another XSP tag:
             preserve its Java type
        -->
        (<xsl:value-of select="."/>)
      </xsl:when>
      <xsl:when test="string-length(.) = 0">
        <!-- Do nothing -->
      </xsl:when>
      <xsl:otherwise>
        <!-- Output the value as elements or character data depending on its type -->
        page.xspExpr(<xsl:value-of select="."/>);
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- FIXME: Is this _really_ necessary? -->
  <xsl:template match="xsp:content">
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="xsp:logic">
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="xsp:pi">
    <xsl:variable name="target">
      <xsl:call-template name="get-parameter">
        <xsl:with-param name="name">target</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="content">
      <xsl:for-each select="text()|xsp:expr">
        <xsl:choose>
          <xsl:when test="namespace-uri(.) = $xsp-uri and local-name(.) = 'expr'">
            String(<xsl:value-of select="."/>)
          </xsl:when>
          <xsl:otherwise>
            "<xsl:value-of select="."/>"
          </xsl:otherwise>
        </xsl:choose>
        <xsp:text> + </xsp:text>
      </xsl:for-each>
      ""
    </xsl:variable>

    contentHandler.processingInstruction(<xsl:copy-of select="$target"/>,
      <xsl:value-of select="normalize-space($content)"/>);
  </xsl:template>


  <!-- FIXME: How to create comments in SAX? -->
  <xsl:template match="xsp:comment">
    page.xspComment("<xsl:value-of select="."/>");
  </xsl:template>


  <!-- $xsp-uri = 'http://apache.org/xsp' -->
  <xsl:template match="*[not(namespace-uri(.) = 'http://apache.org/xsp')]">
    <xsl:variable name="parent-element" select=".."/>
    <xsl:for-each select="namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <xsl:if test="not($parent-element/namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri])">
        contentHandler.startPrefixMapping("<xsl:value-of select="local-name(.)"/>", "<xsl:value-of select="."/>");
      </xsl:if>
    </xsl:for-each>

    <xsl:apply-templates select="@*"/>

    <xsl:apply-templates select="xsp:attribute | xsp:logic[xsp:attribute]"/>

    contentHandler.startElement("<xsl:value-of select="namespace-uri(.)"/>",
      "<xsl:value-of select="local-name(.)"/>", "<xsl:value-of select="name(.)"/>", xspAttr);
    xspAttr.clear();

    <xsl:apply-templates select="node()[not(
        (namespace-uri(.) = $xsp-uri and local-name(.) = 'attribute') or
        (namespace-uri(.) = $xsp-uri and local-name(.) = 'logic' and ./xsp:attribute)
      )]"/>

    contentHandler.endElement("<xsl:value-of select="namespace-uri(.)"/>",
      "<xsl:value-of select="local-name(.)"/>", "<xsl:value-of select="name(.)"/>");

    <xsl:for-each select="namespace::*">
      <xsl:variable name="ns-prefix" select="local-name(.)"/>
      <xsl:variable name="ns-uri" select="string(.)"/>
      <xsl:if test="not($parent-element/namespace::*[local-name(.) = $ns-prefix and string(.) = $ns-uri])">
      contentHandler.endPrefixMapping("<xsl:value-of select="local-name(.)"/>");
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="@*">
    <!-- Filter out namespace declaration attributes -->
    <xsl:if test="not(starts-with(name(.), 'xmlns:'))">
    xspAttr.addAttribute("<xsl:value-of select="namespace-uri(.)"/>",
      "<xsl:value-of select="local-name(.)"/>", "<xsl:value-of select="name(.)"/>",
      "CDATA",
      "<xsl:value-of select="XSLTExtension:escape($extension,.)"/>");
    </xsl:if>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:choose>
      <xsl:when test="namespace-uri(..) = $xsp-uri and (local-name(..) = 'logic' or local-name(..) = 'expr')">
        <xsl:value-of select="."/>
      </xsl:when>
      <xsl:otherwise>
        page.xspCharacters("<xsl:value-of select="XSLTExtension:escapeJava($extension, .)"/>");
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="text()" mode="value">
    <xsl:value-of select="."/>
  </xsl:template>


  <xsl:template match="processing-instruction()">
    contentHandler.processingInstruction("<xsl:value-of select="name()"/>", "<xsl:value-of select="."/>");
  </xsl:template>


  <!-- Utility templates -->
  <xsl:template name="get-parameter">
    <xsl:param name="name"/>
    <xsl:param name="default"/>
    <xsl:param name="required">false</xsl:param>

    <xsl:choose>
      <xsl:when test="@*[name(.) = $name]">"<xsl:value-of select="@*[name(.) = $name]"/>"</xsl:when>
      <xsl:when test="xsp:param[@name = $name]">
        <xsl:call-template name="get-parameter-content">
          <xsl:with-param name="content"
                          select="(*[namespace-uri(.)=$xsp-uri and local-name(.) = 'param'])[@name = $name]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="string-length($default) = 0">
            <xsl:choose>
              <xsl:when test="$required = 'true'">
                <xsl:call-template name="error">
                  <xsl:with-param name="message">[Logicsheet processor]
Parameter '<xsl:value-of select="$name"/>' missing in dynamic tag &lt;<xsl:value-of select="name(.)"/>&gt;
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>""</xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise><xsl:copy-of select="$default"/></xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="get-parameter-content">
    <xsl:param name="content"/>
    <xsl:choose>
      <xsl:when test="$content/*[namespace-uri(.)=$xsp-uri and local-name(.) != 'text']">
        <xsl:apply-templates select="$content/*[namespace-uri(.)=$xsp-uri and local-name(.) != 'text']"/>
      </xsl:when>
      <xsl:otherwise>"<xsl:value-of select="$content"/>"</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="get-nested-content">
    <xsl:param name="content"/>
    <xsl:choose>
      <xsl:when test="$content/*">
        <xsl:apply-templates select="$content/*"/>
      </xsl:when>
      <xsl:otherwise>"<xsl:value-of select="$content"/>"</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="error">
    <xsl:param name="message"/>
    <xsl:message terminate="yes"><xsl:value-of select="$message"/></xsl:message>
  </xsl:template>

  <!-- Ignored elements -->
  <xsl:template match="xsp:logicsheet|xsp:dependency|xsp:param"/>
</xsl:stylesheet>
