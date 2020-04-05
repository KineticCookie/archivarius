package com.lightbend.paradox.markdown.directives

import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

/**
  * Base directive class, for directive specific serialization.
  */
abstract class Directive {
  def names: Seq[String]

  def format: Set[DirectiveNode.Format]

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit
}
