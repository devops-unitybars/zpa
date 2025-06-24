package org.sonar.plugins.plsqlopen.api.sql

import com.felipebz.flr.tests.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sonar.plugins.plsqlopen.api.DmlGrammar
import org.sonar.plugins.plsqlopen.api.RuleTest

class UnpivotClauseTest : RuleTest() {

    @BeforeEach
    fun init() {
        setRootRule(DmlGrammar.UNPIVOT_CLAUSE)
    }

    @Test
    fun matchesSimpleUnpivot() {
        assertThat(p).matches("UNPIVOT (amount FOR quarter IN (Q1,Q2,Q3))")
    }

    @Test
    fun matchesSimpleUnpivotWithAliases() {
        assertThat(p).matches("UNPIVOT (amount FOR quarter IN (Q1 'Q1', Q2 as q2, Q3 as q3))")
    }

    @Test
    fun matchesSimpleUnpivotIncludeNulls() {
        assertThat(p).matches("UNPIVOT INCLUDE NULLS (amount FOR quarter IN (Q1,Q2,Q3))")
    }

    @Test
    fun matchesSimpleUnpivotExcludeNulls() {
        assertThat(p).matches("UNPIVOT EXCLUDE NULLS (amount FOR quarter IN (Q1,Q2,Q3))")
    }

    @Test
    fun matchesSimpleUnpivotWithMultipleColumns() {
        assertThat(p).matches("UNPIVOT (\n" +
            "    (amount, units)\n" +
            "    FOR quarter IN (\n" +
            "    (q1_amount, q1_units) AS 'Q1',\n" +
            "    (q2_amount, q2_units) AS 'Q2'\n" +
            "    )\n" +
            "    )")
    }
}
