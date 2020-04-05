package com.lightbend.paradox.markdown.directives

import org.pegdown.ast.DirectiveNode.Format.ContainerBlock

/**
  * Container block directive.
  */
abstract class ContainerBlockDirective(val names: String*) extends Directive {
  val format = Set(ContainerBlock)
}
