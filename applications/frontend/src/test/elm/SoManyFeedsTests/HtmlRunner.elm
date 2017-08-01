module SoManyFeedsTests.HtmlRunner exposing (main)

import Test.Runner.Html as HtmlRunner
import SoManyFeedsTests.TestSuite as TestSuite


main : HtmlRunner.TestProgram
main =
    HtmlRunner.run TestSuite.suite
