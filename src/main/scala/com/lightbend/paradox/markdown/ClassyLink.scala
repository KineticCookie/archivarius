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

package com.lightbend.paradox.markdown

import org.pegdown.Printer
import org.pegdown.ast.{ClassyLinkNode, _}
import org.pegdown.plugins.ToHtmlSerializerPlugin

import scala.jdk.CollectionConverters._

/**
  * Serialize a ClassyLink, adding the class attribute.
  */
class ClassyLinkSerializer extends ToHtmlSerializerPlugin {
  def visit(node: Node, visitor: Visitor, printer: Printer): Boolean = node match {
    case link: ClassyLinkNode =>
      printer.print(s"""<a href="${link.href}" class="${link.classAttribute}">""")
      link.getChildren.asScala.foreach(_.accept(visitor))
      printer.print("</a>")
      true
    case _ => false
  }
}
