package com.lightbend.paradox.markdown.directives

import org.pegdown.ast.DirectiveNode.Format.Inline

/**
  * Inline directive.
  */
abstract class InlineDirective(val names: String*) extends Directive {
  val format = Set(Inline)
}
