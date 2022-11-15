@Tag
  @singleRoll
  Feature: player plays a single round and then score
    Scenario Outline: player roll eight dice, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When player end turn
      Then player is <dead>
      And player score <score>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | fc |score  | dead |
        |3  |3  |3  |4  |4  |4  |4  |4  |4   |0      |1     |
        |1  |1  |2  |2  |0  |0  |5  |5  |1   |800    |0     |
        |2  |2  |2  |4  |4  |4  |3  |3  |4   |300    |0     |
        |5  |5  |5  |2  |1  |3  |4  |3  |4   |500    |0     |
        |0  |0  |0  |0  |3  |3  |4  |4  |5   |700    |0     |
        |4  |4  |4  |1  |1  |1  |1  |3  |4   |400    |0     |
        |2  |2  |2  |2  |2  |2  |3  |3  |4   |1100   |0     |
        |1  |1  |1  |1  |1  |1  |1  |3  |4   |2100   |0     |
        |0  |0  |0  |0  |0  |0  |0  |0  |4   |5400   |0     |
        |0  |0  |0  |0  |0  |0  |0  |0  |5   |5400   |0     |

    @re-rollOnce
    Scenario Outline: player roll eight dice, re-roll once, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When re-roll dice <index>
      And got new dice <dice>
      And player end turn
      Then player is <dead>
      And player score <score>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | index  | dice   | fc |score  | dead |
        |3  |1  |1  |1  |1  |4  |4  |4  |"5,6,7" |"3,3,4" |4   |0      |1     |
        |3  |3  |1  |1  |1  |1  |4  |4  |"6,7"   |"3,4"   |4   |0      |1     |
        |2  |2  |3  |3  |4  |4  |1  |1  |"6,7"   |"4,2"   |4   |300    |0     |
        |3  |0  |0  |1  |1  |4  |4  |4  |"3,4"   |"4,0"   |4   |800    |0     |
        |3  |0  |0  |1  |1  |4  |4  |4  |"3,4"   |"4,0"   |1   |1200   |0     |


    @re-rollTwice
    Scenario Outline: player roll eight dice, re-roll twice, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When re-roll dice <index1>
      And got new dice <dice1>
      And re-roll dice <index2>
      And got new dice <dice2>
      And player end turn
      Then player is <dead>
      And player score <score>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | fc | index1 | dice1 | index2 | dice2 | score | dead |
        |3  |1  |1  |1  |1  |4  |4  |4  |4   |"5,6,7" |"3,2,2"|"6,7"   |"3,2"  |0      |1     |
        |3  |1  |1  |4  |4  |4  |0  |0  |4   |"1,2"   |"0,0"  |"1,2,3" |"0,0,0"|4800   |0     |
        |3  |1  |1  |2  |2  |4  |4  |4  |4   |"3,4"   |"3,4"  |"1,2"   |"2,4"  |600    |0     |

