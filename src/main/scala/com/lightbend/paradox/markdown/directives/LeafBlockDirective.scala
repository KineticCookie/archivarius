package com.lightbend.paradox.markdown.directives

import org.pegdown.ast.DirectiveNode.Format.LeafBlock

/**
  * Leaf block directive.
  */
abstract class LeafBlockDirective(val names: String*) extends Directive {
  val format = Set(LeafBlock)
}
