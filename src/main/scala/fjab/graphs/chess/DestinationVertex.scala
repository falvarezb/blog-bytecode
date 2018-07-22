package fjab.graphs.chess

import fjab.graphs.api.GraphTraversal

trait DestinationVertex[T] extends GraphTraversal[T]{
  def to(): Vertex
}
