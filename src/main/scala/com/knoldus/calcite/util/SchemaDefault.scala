package com.knoldus.calcite.util

import java.util.Properties

import org.apache.calcite.config.{CalciteConnectionConfig, CalciteConnectionConfigImpl}
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.plan.{RelOptCluster, RelOptTable}
import org.apache.calcite.prepare.CalciteCatalogReader
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory, RelDataTypeSystem}
import org.apache.calcite.rel.{RelNode, RelRoot}
import org.apache.calcite.rex.RexBuilder
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.SqlNode
import org.apache.calcite.sql.`type`.{SqlTypeFactoryImpl, SqlTypeName}
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.util.ChainedSqlOperatorTable
import org.apache.calcite.sql.validate.{SqlConformance, SqlConformanceEnum}
import org.apache.calcite.sql2rel.{RelDecorrelator, SqlToRelConverter}
import org.apache.calcite.tools.FrameworkConfig

object SchemaDefault {
  def getSchema(rootSchema: CalciteSchema): CalciteSchema = {
    rootSchema.add(
      "USERS",
      new AbstractTable() {
        override def getRowType(
                                 typeFactory: RelDataTypeFactory
                               ): RelDataType = {
          val builder = typeFactory.builder
          val t1 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.INTEGER),
            true
          )
          val t2 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.CHAR),
            true
          )
          val t3 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.CHAR),
            true
          )
          builder.add("ID", t1)
          builder.add("NAME", t2)
          builder.add("OWNERID", t3)
          builder.build
        }
      }
    )

    rootSchema.add(
      "ADDRESS",
      new AbstractTable() {
        override def getRowType(
                                 typeFactory: RelDataTypeFactory
                               ): RelDataType = {
          val builder1 = typeFactory.builder
          val address_t1 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.INTEGER),
            true
          )
          val address_t2 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.CHAR),
            true
          )
          builder1.add("ID", address_t1)
          builder1.add("ADDRESSES", address_t2)
          builder1.build
        }
      }
    )
    rootSchema.add(
      "T",
      new AbstractTable() {
        override def getRowType(
                                 typeFactory: RelDataTypeFactory
                               ): RelDataType = {
          val builder1 = typeFactory.builder
          val address_t1 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.INTEGER),
            true
          )
          val address_t2 = typeFactory.createTypeWithNullability(
            typeFactory.createSqlType(SqlTypeName.CHAR),
            true
          )
          builder1.add("X", address_t1)
          builder1.add("Y", address_t1)
          builder1.add("Z", address_t1)
          builder1.build
        }
      }
    )
    rootSchema
  }


  def sqlToRelNode(
                    rootScheme: SchemaPlus,
                    frameworkConfig: FrameworkConfig,
                    sql: String
                  ): RelNode = {

    try {
      val factory: SqlTypeFactoryImpl = new SqlTypeFactoryImpl(
        RelDataTypeSystem.DEFAULT
      )
      val parser: SqlParser =
        SqlParser.create(sql, frameworkConfig.getParserConfig)
      val sqlNode: SqlNode = parser.parseStmt

      val calciteCatalogReader = new CalciteCatalogReader(
        CalciteSchema.from(rootScheme),
        CalciteSchema.from(frameworkConfig.getDefaultSchema).path(null),
        factory,
        new CalciteConnectionConfigImpl(new Properties)
      )

      //to supported user' define function
      val sqlOperatorTable = ChainedSqlOperatorTable.of(
        frameworkConfig.getOperatorTable,
        calciteCatalogReader
      )

      val validator: CalciteSqlValidator = new CalciteSqlValidator(
        sqlOperatorTable,
        calciteCatalogReader,
        factory,
        conformance(frameworkConfig)
      )

      val validateSqlNode = validator.validate(sqlNode)
      val rexBuilder = createRexBuilder(factory)

      val volcanoPlanner = new CalciteVolcanoPlanner()
      volcanoPlanner.clearRelTraitDefs()

      import collection.JavaConverters._
      for (defs <- frameworkConfig.getTraitDefs.asScala) {
        volcanoPlanner.addRelTraitDef(defs)
      }

      //An environment for related relational expressions during the optimization of a query.
      val cluster: RelOptCluster = RelOptCluster.create(volcanoPlanner, rexBuilder)


      val config = SqlToRelConverter.configBuilder
        .withConfig(frameworkConfig.getSqlToRelConverterConfig)
        .withTrimUnusedFields(false)
        .build

      val sqlToRelConverter = new SqlToRelConverter(
        new CalciteView,
        validator,
        calciteCatalogReader,
        cluster,
        frameworkConfig.getConvertletTable,
        config
      )

      var root: RelRoot = sqlToRelConverter.convertQuery(validateSqlNode, false, true)
      root = root.withRel(sqlToRelConverter.flattenTypes(root.rel, true))

      val relBuilder = config.getRelBuilderFactory.create(cluster, null)
      root = root.withRel(RelDecorrelator.decorrelateQuery(root.rel, relBuilder))
      println("got the Relational-Root=>\n" + root)
      root.rel
    } catch {
      case e: Exception =>
        e.printStackTrace()
        null
    }
  }

  private def conformance(config: FrameworkConfig): SqlConformance = {
    val context = config.getContext
    if (context != null) {
      val connectionConfig = context.unwrap(classOf[CalciteConnectionConfig])
      if (connectionConfig != null) return connectionConfig.conformance
    }
    SqlConformanceEnum.DEFAULT
  }

  private def createRexBuilder(typeFactory: RelDataTypeFactory) =
    new RexBuilder(typeFactory)

}

private class CalciteView() extends RelOptTable.ViewExpander {
  override def expandView(
                           rowType: RelDataType,
                           queryString: String,
                           schemaPath: java.util.List[String],
                           viewPath: java.util.List[String]
                         ): RelRoot = {
    null
  }
}