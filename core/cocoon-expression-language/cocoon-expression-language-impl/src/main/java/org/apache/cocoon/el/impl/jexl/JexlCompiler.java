/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.el.impl.jexl;

import org.apache.cocoon.el.Expression;
import org.apache.cocoon.el.ExpressionCompiler;
import org.apache.cocoon.el.ExpressionException;

/**
 * @version $Id: JexlCompiler.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
public class JexlCompiler implements ExpressionCompiler {

    /**
     * @see org.apache.cocoon.el.ExpressionCompiler#compile(java.lang.String, java.lang.String)
     */
    public Expression compile(String language, String expression) throws ExpressionException {
        return new JexlExpression(language, expression);
    }
}
