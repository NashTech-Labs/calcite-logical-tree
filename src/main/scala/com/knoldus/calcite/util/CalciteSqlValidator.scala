package com.knoldus.calcite.util

import org.apache.calcite.rel.`type`.RelDataTypeFactory
import org.apache.calcite.sql.SqlOperatorTable
import org.apache.calcite.sql.validate.{SqlConformance, SqlValidatorCatalogReader, SqlValidatorImpl}

class CalciteSqlValidator(
  opTab: SqlOperatorTable,
  catalogReader: SqlValidatorCatalogReader,
  typeFactory: RelDataTypeFactory,
  conformance: SqlConformance
) extends SqlValidatorImpl(opTab, catalogReader, typeFactory, conformance)
