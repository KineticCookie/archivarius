/*
 * Copyright © 2015 - 2019 Lightbend, Inc. <http://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.paradox.markdown.directives

import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, _}
import org.pegdown.plugins.ToHtmlSerializerPlugin

/**
  * Serialize directives, checking the name and format against registered directives.
  */
class DirectiveSerializer(directives: Seq[Directive]) extends ToHtmlSerializerPlugin {
  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val directiveMap = directives.flatMap(d => d.names.map(n => (n, d))).toMap

  def visit(node: Node, visitor: Visitor, printer: Printer): Boolean = node match {
    case dnode: DirectiveNode =>
      directiveMap.get(dnode.name) match {
        case Some(directive) if directive.format(dnode.format) =>
          directive.render(dnode, visitor, printer)
        case x =>
          logger.warn(s"Unknown directive: $dnode") // printer.print(s"<!-- $dnode -->")
      }
      true
    case _ => false
  }
}
