package fjab.graphs.chess

import fjab.graphs._
import fjab.graphs.api.SharedKnowledgeGraphTraversal

case class SKShortestPathInInfiniteBoardApp(to: Coordinate) extends SharedKnowledgeGraphTraversal[Coordinate] with InfiniteChessBoard with ShortestPath with Destination[Coordinate]

object SKShortestPathInInfiniteBoardApp{
  def apply(to: Coordinate): SKShortestPathInInfiniteBoardApp = new SKShortestPathInInfiniteBoardApp(to)
}