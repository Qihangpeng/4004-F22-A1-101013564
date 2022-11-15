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
        |4  |4  |4  |4  |4  |4  |4  |4  |1   |9000   |0     |
        |2  |2  |2  |2  |0  |0  |3  |3  |4   |600    |0     |
        |2  |2  |2  |1  |1  |1  |3  |0  |6   |1100   |0     |
        |3  |3  |3  |2  |2  |2  |1  |1  |6   |0      |1     |
        |2  |2  |2  |4  |4  |4  |1  |5  |4   |400    |0     |
        |2  |2  |2  |4  |4  |4  |0  |0  |1   |1800   |0     |
        |2  |2  |2  |4  |4  |4  |4  |5  |4   |1000   |0     |
        |2  |2  |1  |0  |0  |5  |5  |5  |6   |1200   |0     |
        |3  |4  |4  |4  |4  |4  |4  |4  |72  |0      |1     |
        |3  |3  |4  |4  |4  |4  |4  |4  |71  |0      |1     |
        |2  |2  |2  |2  |3  |3  |3  |4  |32  |-300   |1     |
        |2  |2  |3  |3  |3  |4  |4  |4  |34  |-1000  |1     |
        |2  |2  |2  |4  |4  |0  |1  |1  |32  |500    |0     |
        |2  |2  |2  |4  |4  |4  |4  |3  |33  |800    |0     |
        |2  |2  |2  |4  |4  |4  |4  |3  |34  |1300   |0     |

    @re-rollOnce
    Scenario Outline: player roll eight dice, re-roll once, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When re-roll dice <index> and got new dice <dice>
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
        |2  |2  |2  |2  |2  |2  |4  |4  |"6,7"   |"2,2"   |4   |4600   |0     |
        |2  |2  |3  |3  |4  |4  |1  |1  |"6,7"   |"5,5"   |5   |400    |0     |
        |2  |2  |3  |3  |4  |4  |5  |1  |"0,1"   |"5,5"   |4   |500    |0     |
        |3  |0  |0  |2  |1  |4  |4  |4  |"5,6,7,"|"0,1,2" |4   |600    |0     |
        |3  |0  |0  |2  |1  |4  |4  |4  |"5,6,7,"|"0,1,2" |5   |500    |0     |
        |2  |2  |1  |1  |4  |4  |0  |0  |"4,5"   |"1,2"   |6   |1700   |0     |
        |2  |2  |2  |2  |4  |1  |1  |0  |"5,6"   |"4,0"   |32  |1200   |0     |
        |3  |3  |3  |3  |3  |2  |2  |2  |"5,6,7" |"3,3,0" |1   |0      |0     |
        |3  |3  |3  |4  |4  |4  |4  |4  |"3,4,5,6,7"|"0,0,0,0,0"|72|0  |0     |
        |4  |4  |2  |2  |1  |1  |1  |1  |"4,5,6,7"  |"3,3,3,3," |33|-500|1    |
        |2  |2  |2  |2  |4  |3  |1  |1  |"6,7"      |"3,4"      |32|500 |0    |
        |2  |2  |2  |2  |3  |3  |4  |4  |"0,1,2,3"  |"3,3,4,4"  |33|-500|1    |


    @re-rollTwice
    Scenario Outline: player roll eight dice, re-roll twice, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When re-roll dice <index1> and got new dice <dice1>
      And re-roll dice <index2> and got new dice <dice2>
      And player end turn
      Then player is <dead>
      And player score <score>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | fc | index1 | dice1 | index2 | dice2 | score | dead |
        |3  |1  |1  |1  |1  |4  |4  |4  |4   |"5,6,7" |"3,2,2"|"6,7"   |"3,2"  |0      |1     |
        |3  |1  |1  |4  |4  |4  |0  |0  |4   |"1,2"   |"0,0"  |"1,2,3" |"0,0,0"|4800   |0     |
        |3  |1  |1  |2  |2  |4  |4  |4  |4   |"3,4"   |"3,4"  |"1,2"   |"2,4"  |600    |0     |
        |5  |5  |4  |2  |0  |1  |1  |1  |2   |"5,6,7" |"2,2,3"|"7"     |"2"    |500    |0     |
        |3  |3  |3  |1  |1  |1  |4  |4  |2   |"0"     |"1"    |"5,6"   |"1,1"  |1000   |0     |
        |3  |1  |1  |1  |1  |2  |2  |2  |2   |"5,6,7" |"3,1,1"|"0"     |"1"    |2000   |0     |
        |1  |1  |1  |4  |4  |5  |5  |0  |0   |"3,4"   |"1,1"  |"3,4,5,"|"3,0,1"|1100   |0     |
        |3  |3  |1  |1  |1  |0  |0  |0  |0   |"2,3,4" |"5,5,0"|"5,6"   |"3,0"  |600    |1     |
        |3  |3  |1  |1  |1  |2  |2  |2  |72  |"2,3,4" |"3,3,4"|"2,3,4,7"|"3,3,3,4"|0   |0     |
        |2  |2  |2  |3  |4  |5  |1  |1  |34  |"6,7"   |"4,4"  |"0,1,2"  |"4,1,1"  |1300|0     |

    @multi-player
    Feature: networked tests
      Scenario Outline: 3 players play a cheated game
        Given server is running with cheat command <commandS>
        When player 1 starts with cheat command <command1>
        And player 2 starts with cheat command <command2>
        And player 3 starts with cheat command <command3>
        Then game ends with winner <winner>
        Examples:
        
