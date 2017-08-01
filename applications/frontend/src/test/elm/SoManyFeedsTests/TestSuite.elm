module SoManyFeedsTests.TestSuite exposing (..)

import Test exposing (..)
import Expect
import String
import SoManyFeedsTests.DateFormat
import SoManyFeedsTests.Feeds
import SoManyFeedsTests.Articles


suite : Test
suite =
    describe "So Many Feeds"
        [ SoManyFeedsTests.DateFormat.tests
        , SoManyFeedsTests.Feeds.tests
        , SoManyFeedsTests.Articles.tests
        ]
