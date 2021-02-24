package com.knoldus.calcite.app

import com.knoldus.calcite.util.SchemaDefault
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.plan.{ConventionTraitDef, RelOptUtil}
import org.apache.calcite.rel.{RelCollationTraitDef, RelDistributionTraitDef, RelNode}
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.tools.{FrameworkConfig, Frameworks}

object Main extends App {

  val rootSchema = CalciteSchema.createRootSchema(false, false)
  val testDefaultSchema = SchemaDefault.getSchema(rootSchema).plus()

  val config: FrameworkConfig = Frameworks.newConfigBuilder
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(testDefaultSchema)
    .traitDefs(ConventionTraitDef.INSTANCE, RelDistributionTraitDef.INSTANCE, RelCollationTraitDef.INSTANCE)
    .build

  private val groupByQuery: String = "SELECT ID FROM USERS GROUP BY ID"
  private var relNode: RelNode = SchemaDefault.sqlToRelNode(testDefaultSchema, config, groupByQuery)

  println("-----------------------------------------------------------")
  println("Logical plan for the query=\n " + RelOptUtil.toString(relNode))
  println("-----------------------------------------------------------")

}
