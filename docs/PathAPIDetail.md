# Path API 예시
## API 바로가기
| API 호출                                   | 설명               |
|------------------------------------------|------------------|
| [POST /path/pedestrian](#스케줄로-보행자-길찾기)   | 스케줄로 보행자 길찾기     |
| [POST /path/car](#스케줄로-자동차-길찾기)          | 스케줄로 자동차 길찾기     |
| [POST /path/transit](#스케줄로-대중교통-길찾기)     | 스케줄로 대중교통 길찾기    |
| [GET /path/pedestrian](#장소-이름으로-보행자-길찾기) | 장소 이름으로 보행자 길찾기  |
| [GET /path/car](#장소-이름으로-자동차-길찾기)        | 장소 이름으로 자동차 길찾기  |
| [GET /path/transit](#장소-이름으로-대중교통-길찾기)   | 장소 이름으로 대중교통 길찾기 |

---

## API 상세

### 스케줄로 보행자 길찾기

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/path/pedestrian`,
    [
        {
            "scheduleContent": "경복궁 방문",
            "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
            "latitude": 37.5760836609,
            "longitude": 126.9767375783,
            "scheduleStartTime": "2025-06-01T10:00:00",
            "scheduleEndTime": "2025-06-01T11:00:00"
        },
        {
            "scheduleContent": "창경궁 방문",
            "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
            "latitude": 37.5776782272,
            "longitude": 126.9938554166,
            "scheduleStartTime": "2025-06-01T11:30:00",
            "scheduleEndTime": "2025-06-01T12:30:00"
        },
        {
            "scheduleContent": "진옥화할매원조닭한마리 방문",
            "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
            "latitude": 37.5704292825,
            "longitude": 127.0057128756,
            "scheduleStartTime": "2025-06-01T13:00:00",
            "scheduleEndTime": "2025-06-01T14:00:00"
        },
        {
            "scheduleContent": "청계천 방문",
            "scheduleAddress": "서울특별시 종로구 창신동 ",
            "latitude": 37.5697015781,
            "longitude": 127.0050907302,
            "scheduleStartTime": "2025-06-01T14:30:00",
            "scheduleEndTime": "2025-06-01T15:30:00"
        }
    ]
)
```

#### Response 예시
```json
[
    {
        "origin": {
          "name": "경복궁",
          "x": 126.9767375783,
          "y": 37.5760836609
        },
        "destination": {
          "name": "창경궁",
          "x": 126.9938554166,
          "y": 37.5776782272
        },
        "distance": 2184,
        "time": 29,
        "fare": null,
        "coordinates": [
            [
                126.9768181264162,
                37.57608131977726
            ],
            [
                126.97677092934445,
                37.57534529341477
            ]
        ]
    },
    {
        "origin": {
            "name": "창경궁",
            "x": 126.9938554166,
            "y": 37.5776782272
        },
        "destination": {
            "name": "진옥화할매원조닭한마리",
            "x": 127.0057128756,
            "y": 37.5704292825
        },
        "distance": 2049,
        "time": 27,
        "fare": null,
        "coordinates": [
            [
                126.99381375693515,
                37.57758700532683
            ],
            [
                126.99422483365203,
                37.57747869197145
            ]
        ]
    },
    {
        "origin": {
            "name": "진옥화할매원조닭한마리",
            "x": 127.0057128756,
            "y": 37.5704292825
        },
        "destination": {
            "name": "청계천",
            "x": 127.0050907302,
            "y": 37.5697015781
        },
        "distance": 304,
        "time": 4,
        "fare": null,
        "coordinates": [
            [
                127.00582675399545,
                37.57042972003023
            ],
            [
                127.0058267578539,
                37.5702936247437
            ]
        ]
    }
]
```

---

### 스케줄로 자동차 길찾기

#### Request 예시
```javascript
axios.post(`${API_BASE_URL}/path/car`,
    [
        {
            "scheduleContent": "경복궁 방문",
            "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
            "latitude": 37.5760836609,
            "longitude": 126.9767375783,
            "scheduleStartTime": "2025-06-01T10:00:00",
            "scheduleEndTime": "2025-06-01T11:00:00"
        },
        {
            "scheduleContent": "창경궁 방문",
            "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
            "latitude": 37.5776782272,
            "longitude": 126.9938554166,
            "scheduleStartTime": "2025-06-01T11:30:00",
            "scheduleEndTime": "2025-06-01T12:30:00"
        },
        {
            "scheduleContent": "진옥화할매원조닭한마리 방문",
            "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
            "latitude": 37.5704292825,
            "longitude": 127.0057128756,
            "scheduleStartTime": "2025-06-01T13:00:00",
            "scheduleEndTime": "2025-06-01T14:00:00"
        },
        {
            "scheduleContent": "청계천 방문",
            "scheduleAddress": "서울특별시 종로구 창신동 ",
            "latitude": 37.5697015781,
            "longitude": 127.0050907302,
            "scheduleStartTime": "2025-06-01T14:30:00",
            "scheduleEndTime": "2025-06-01T15:30:00"
        }
    ]
)
```

#### Response 예시
```json
[
  {
    "origin": {
      "name": "경복궁",
      "x": 126.9767375783,
      "y": 37.5760836609
    },
    "destination": {
      "name": "창경궁",
      "x": 126.9938554166,
      "y": 37.5776782272
    },
    "distance": 3963,
    "time": 15,
    "fare": 7600,
    "coordinates": [
      [
        126.97629597137697,
        37.57538972416959
      ],
      [
        126.97602376916649,
        37.5755591440272
      ]
    ]
  },
  {
    "origin": {
      "name": "창경궁",
      "x": 126.9938554166,
      "y": 37.5776782272
    },
    "destination": {
      "name": "진옥화할매원조닭한마리",
      "x": 127.0057128756,
      "y": 37.5704292825
    },
    "distance": 3266,
    "time": 11,
    "fare": 6800,
    "coordinates": [
      [
        126.99371655034373,
        37.57734814246957
      ],
      [
        126.99339157610346,
        37.57748423191904
      ]
    ]
  },
  {
    "origin": {
      "name": "진옥화할매원조닭한마리",
      "x": 127.0057128756,
      "y": 37.5704292825
    },
    "destination": {
      "name": "청계천",
      "x": 127.0050907302,
      "y": 37.5697015781
    },
    "distance": 810,
    "time": 1,
    "fare": 4800,
    "coordinates": [
      [
        127.0056739741527,
        37.57099354061587
      ],
      [
        127.00649056593643,
        37.571010220013164
      ]
    ]
  }
]
```

---

### 스케줄로 대중교통 길찾기

#### Request 예시 1 (정상 반환)
```javascript
axios.post(`${API_BASE_URL}/path/transit`,
    [
        {
            "scheduleContent": "경복궁 방문",
            "scheduleAddress": "서울특별시 종로구 사직로 161 (세종로) ",
            "latitude": 37.5760836609,
            "longitude": 126.9767375783,
            "scheduleStartTime": "2025-06-01T10:00:00",
            "scheduleEndTime": "2025-06-01T11:00:00"
        },
        {
            "scheduleContent": "창경궁 방문",
            "scheduleAddress": "서울특별시 종로구 창경궁로 185 ",
            "latitude": 37.5776782272,
            "longitude": 126.9938554166,
            "scheduleStartTime": "2025-06-01T11:30:00",
            "scheduleEndTime": "2025-06-01T12:30:00"
        },
        {
            "scheduleContent": "진옥화할매원조닭한마리 방문",
            "scheduleAddress": "서울특별시 종로구 종로40가길 18 (종로5가) ",
            "latitude": 37.5704292825,
            "longitude": 127.0057128756,
            "scheduleStartTime": "2025-06-01T13:00:00",
            "scheduleEndTime": "2025-06-01T14:00:00"
        },
        {
            "scheduleContent": "청계천 방문",
            "scheduleAddress": "서울특별시 종로구 창신동 ",
            "latitude": 37.5697015781,
            "longitude": 127.0050907302,
            "scheduleStartTime": "2025-06-01T14:30:00",
            "scheduleEndTime": "2025-06-01T15:30:00"
        }
    ]
)
```

#### Response 예시 1 (정상 반환, 경복궁->창경궁 부분만)
```json
[
  {
    "origin": {
      "name": "경복궁",
      "x": 126.9767375783,
      "y": 37.5760836609
    },
    "destination": {
      "name": "창경궁",
      "x": 126.9938554166,
      "y": 37.5776782272
    },
    "plan": [
      {
        "totalDistance": 1553,
        "totalTime": 27,
        "totalWalkTime": 21,
        "transferCount": 0,
        "fare": 1500,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "경복궁까지 도보로 이동",
            "distance": 292,
            "time": 4,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 126.9767375783,
              "y": 37.5760836609
            },
            "end": {
              "name": "경복궁",
              "x": 126.97874722222222,
              "y": 37.57561666666667
            },
            "coordinates": [
              [
                126.97682,
                37.57608
              ],
              [
                126.977425,
                37.575203
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "경복궁 승차 - 지선:7025 - 간선:109 - 창덕궁.돈화문국악당 하차",
            "distance": 1120,
            "time": 6,
            "routeColor": "0068B7",
            "routeStyle": "solid",
            "start": {
              "name": "경복궁",
              "x": 126.97874722222222,
              "y": 37.57561666666667
            },
            "end": {
              "name": "창덕궁.돈화문국악당",
              "x": 126.99106666666667,
              "y": 37.57743333333333
            },
            "coordinates": [
              [
                126.978717,
                37.575683
              ],
              [
                126.979272,
                37.575756
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 1284,
            "time": 16,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "창덕궁.돈화문국악당",
              "x": 126.99106666666667,
              "y": 37.57743333333333
            },
            "end": {
              "name": "도착지",
              "x": 126.9938554166,
              "y": 37.5776782272
            },
            "coordinates": [
              [
                126.991066,
                37.577377
              ],
              [
                126.99141,
                37.577404
              ]
            ]
          }
        ]
      },
      {
        "totalDistance": 1552,
        "totalTime": 22,
        "totalWalkTime": 18,
        "transferCount": 0,
        "fare": 1500,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "경복궁까지 도보로 이동",
            "distance": 292,
            "time": 4,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 126.9767375783,
              "y": 37.5760836609
            },
            "end": {
              "name": "경복궁",
              "x": 126.97874722222222,
              "y": 37.57561666666667
            },
            "coordinates": [
              [
                126.97682,
                37.57608
              ],
              [
                126.977425,
                37.575203
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "경복궁 승차 - 간선:171 - 간선:601 - 간선:710 - 지선:7025 - 창덕궁.우리소리박물관 하차",
            "distance": 935,
            "time": 4,
            "routeColor": "0068B7",
            "routeStyle": "solid",
            "start": {
              "name": "경복궁",
              "x": 126.97874722222222,
              "y": 37.57561666666667
            },
            "end": {
              "name": "창덕궁.우리소리박물관",
              "x": 126.98898888888888,
              "y": 37.577283333333334
            },
            "coordinates": [
              [
                126.978717,
                37.575683
              ],
              [
                126.979272,
                37.575756
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 956,
            "time": 13,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "창덕궁.우리소리박물관",
              "x": 126.98898888888888,
              "y": 37.577283333333334
            },
            "end": {
              "name": "도착지",
              "x": 126.9938554166,
              "y": 37.5776782272
            },
            "coordinates": [
              [
                126.98899,
                37.577236
              ],
              [
                126.9891,
                37.57724
              ]
            ]
          }
        ]
      }
    ],
    "message": null
  }
]
```

---

### 장소 이름으로 보행자 길찾기

#### Request 예시 1 (출발지-도착지)
```javascript
axios.get(`${API_BASE_URL}/path/pedestrian?start=한성대학교&end=서울 종로구 대학로 101`)
```

#### Response 예시 1 (출발지-도착지)
```json
[
    {
        "origin": {
          "name": "경복궁",
          "x": 126.9767375783,
          "y": 37.5760836609
        },
        "destination": {
          "name": "창경궁",
          "x": 126.9938554166,
          "y": 37.5776782272
        },
        "distance": 2184,
        "time": 29,
        "fare": null,
        "coordinates": [
            [
                126.9768181264162,
                37.57608131977726
            ],
            [
                126.97677092934445,
                37.57534529341477
            ]
        ]
    },
    {
        "origin": {
            "name": "창경궁",
            "x": 126.9938554166,
            "y": 37.5776782272
        },
        "destination": {
            "name": "진옥화할매원조닭한마리",
            "x": 127.0057128756,
            "y": 37.5704292825
        },
        "distance": 2049,
        "time": 27,
        "fare": null,
        "coordinates": [
            [
                126.99381375693515,
                37.57758700532683
            ],
            [
                126.99422483365203,
                37.57747869197145
            ]
        ]
    },
    {
        "origin": {
            "name": "진옥화할매원조닭한마리",
            "x": 127.0057128756,
            "y": 37.5704292825
        },
        "destination": {
            "name": "청계천",
            "x": 127.0050907302,
            "y": 37.5697015781
        },
        "distance": 304,
        "time": 4,
        "fare": null,
        "coordinates": [
            [
                127.00582675399545,
                37.57042972003023
            ],
            [
                127.0058267578539,
                37.5702936247437
            ]
        ]
    }
]
```

#### Request 예시 2 (중간 지점)
```javascript
axios.get(`${API_BASE_URL}/path/pedestrian?name=한성대학교&name=시청역`)
```

#### Response 예시 2 (중간 지점)
```json
[
  {
    "origin": {
      "name": "한성대학교",
      "x": 127.010225523923,
      "y": 37.5825624632779
    },
    "destination": {
      "name": "중간 지점",
      "x": 126.99371186736082,
      "y": 37.57395392982104
    },
    "distance": 2997,
    "time": 39,
    "fare": null,
    "coordinates": [
      [
        127.01030100198318,
        37.582567277929705
      ],
      [
        127.01031211515524,
        37.58245895739267
      ]
    ]
  },
  {
    "origin": {
      "name": "시청역",
      "x": 126.97719821079865,
      "y": 37.56534539636417
    },
    "destination": {
      "name": "중간 지점",
      "x": 126.99371186736082,
      "y": 37.57395392982104
    },
    "distance": 2531,
    "time": 34,
    "fare": null,
    "coordinates": [
      [
        126.9771350682761,
        37.56534368522587
      ],
      [
        126.97713228839262,
        37.56542700882046
      ]
    ]
  }
]
```

---

### 장소 이름으로 자동차 길찾기

#### Request 예시 1 (출발지-도착지)
```javascript
axios.get(`${API_BASE_URL}/path/car?start=한성대학교&end=서울 종로구 대학로 101`)
```

#### Response 예시 1 (출발지-도착지)
```json
[
  {
    "origin": {
      "name": "한성대학교",
      "x": 127.010225523923,
      "y": 37.5825624632779
    },
    "destination": {
      "name": "서울 종로구 대학로 101",
      "x": 126.997196437908,
      "y": 37.5804523853297
    },
    "distance": 3516,
    "time": 13,
    "fare": 7200,
    "coordinates": [
      [
        127.01021490210772,
        37.58244784582652
      ],
      [
        127.01028711766982,
        37.582450624579074
      ]
    ]
  }
]
```

#### Request 예시 2 (중간 지점)
```javascript
axios.get(`${API_BASE_URL}/path/car?name=한성대학교&name=시청역`)
```

#### Response 예시 2 (중간 지점)
```json
[
  {
    "origin": {
      "name": "한성대학교",
      "x": 127.010225523923,
      "y": 37.5825624632779
    },
    "destination": {
      "name": "중간 지점",
      "x": 126.99371186736082,
      "y": 37.57395392982104
    },
    "distance": 4552,
    "time": 16,
    "fare": 8200,
    "coordinates": [
      [
        127.01021490210772,
        37.58244784582652
      ],
      [
        127.01028711766982,
        37.582450624579074
      ]
    ]
  },
  {
    "origin": {
      "name": "시청역",
      "x": 126.97719821079865,
      "y": 37.56534539636417
    },
    "destination": {
      "name": "중간 지점",
      "x": 126.99371186736082,
      "y": 37.57395392982104
    },
    "distance": 3126,
    "time": 12,
    "fare": 7100,
    "coordinates": [
      [
        126.97714617845334,
        37.565340907970345
      ],
      [
        126.9771489577864,
        37.56527702655945
      ]
    ]
  }
]
```

---

### 장소 이름으로 대중교통 길찾기

#### Request 예시 1 (출발지-도착지)
```javascript
axios.get(`${API_BASE_URL}/path/transit?start=한성대학교&end=서울 종로구 대학로 101`)
```

#### Response 예시 1 (출발지-도착지)
```json
[
  {
    "origin": {
      "name": "한성대학교",
      "x": 127.010225523923,
      "y": 37.5825624632779
    },
    "destination": {
      "name": "서울 종로구 대학로 101",
      "x": 126.997196437908,
      "y": 37.5804523853297
    },
    "plan": [
      {
        "totalDistance": 2504,
        "totalTime": 22,
        "totalWalkTime": 13,
        "transferCount": 1,
        "fare": 1500,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "한양슈퍼앞까지 도보로 이동",
            "distance": 230,
            "time": 3,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 127.010225523923,
              "y": 37.5825624632779
            },
            "end": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "coordinates": [
              [
                127.0103,
                37.58257
              ],
              [
                127.010315,
                37.58246
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "한양슈퍼앞 승차 - 마을:성북02 - 성북세무서앞 하차",
            "distance": 509,
            "time": 4,
            "routeColor": "53B332",
            "routeStyle": "solid",
            "start": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "end": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "coordinates": [
              [
                127.011139,
                37.58355
              ],
              [
                127.011058,
                37.584642
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "삼선교.한성대학교까지 도보로 이동",
            "distance": 312,
            "time": 4,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "end": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "coordinates": [
              [
                127.010578,
                37.588094
              ],
              [
                127.010469,
                37.588281
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "삼선교.한성대학교 승차 - 간선:143 - 간선:162 - 간선:140 - 간선:104 - 명륜3가.성대입구 하차",
            "distance": 1248,
            "time": 5,
            "routeColor": "0068B7",
            "routeStyle": "solid",
            "start": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "end": {
              "name": "명륜3가.성대입구",
              "x": 126.99845277777777,
              "y": 37.582908333333336
            },
            "coordinates": [
              [
                127.009228,
                37.589908
              ],
              [
                127.006175,
                37.588575
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 406,
            "time": 5,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "명륜3가.성대입구",
              "x": 126.99845277777777,
              "y": 37.582908333333336
            },
            "end": {
              "name": "도착지",
              "x": 126.997196437908,
              "y": 37.5804523853297
            },
            "coordinates": [
              [
                126.998436,
                37.582924
              ],
              [
                126.99836,
                37.582863
              ]
            ]
          }
        ]
      },
      {
        "totalDistance": 1416,
        "totalTime": 33,
        "totalWalkTime": 31,
        "transferCount": 0,
        "fare": 1200,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "낙산삼거리까지 도보로 이동",
            "distance": 519,
            "time": 6,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 127.010225523923,
              "y": 37.5825624632779
            },
            "end": {
              "name": "낙산삼거리",
              "x": 127.01037777777778,
              "y": 37.580308333333335
            },
            "coordinates": [
              [
                127.0103,
                37.58257
              ],
              [
                127.010315,
                37.58246
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "낙산삼거리 승차 - 마을:종로03 - 낙산공원 하차",
            "distance": 102,
            "time": 1,
            "routeColor": "53B332",
            "routeStyle": "solid",
            "start": {
              "name": "낙산삼거리",
              "x": 127.01037777777778,
              "y": 37.580308333333335
            },
            "end": {
              "name": "낙산공원",
              "x": 127.00923055555556,
              "y": 37.58026111111111
            },
            "coordinates": [
              [
                127.010394,
                37.580275
              ],
              [
                127.010375,
                37.580264
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 2004,
            "time": 25,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "낙산공원",
              "x": 127.00923055555556,
              "y": 37.58026111111111
            },
            "end": {
              "name": "도착지",
              "x": 126.997196437908,
              "y": 37.5804523853297
            },
            "coordinates": [
              [
                127.00926,
                37.58033
              ],
              [
                127.00894,
                37.580418
              ]
            ]
          }
        ]
      }
    ],
    "message": null
  }
]
```

#### Request 예시 2 (중간 지점)
```javascript
axios.get(`${API_BASE_URL}/path/transit?name=한성대학교&name=시청역`)
```

#### Response 예시 2 (중간 지점, plan 1개만)
```json
[
  {
    "origin": {
      "name": "한성대학교",
      "x": 127.010225523923,
      "y": 37.5825624632779
    },
    "destination": {
      "name": "중간 지점",
      "x": 126.99371186736082,
      "y": 37.57395392982104
    },
    "plan": [
      {
        "totalDistance": 3514,
        "totalTime": 31,
        "totalWalkTime": 17,
        "transferCount": 1,
        "fare": 1500,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "한양슈퍼앞까지 도보로 이동",
            "distance": 230,
            "time": 3,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 127.010225523923,
              "y": 37.5825624632779
            },
            "end": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "coordinates": [
              [
                127.0103,
                37.58257
              ],
              [
                127.010315,
                37.58246
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "한양슈퍼앞 승차 - 마을:성북02 - 성북세무서앞 하차",
            "distance": 509,
            "time": 4,
            "routeColor": "53B332",
            "routeStyle": "solid",
            "start": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "end": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "coordinates": [
              [
                127.011139,
                37.58355
              ],
              [
                127.011058,
                37.584642
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "삼선교.한성대학교까지 도보로 이동",
            "distance": 312,
            "time": 4,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "end": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "coordinates": [
              [
                127.010578,
                37.588094
              ],
              [
                127.010469,
                37.588281
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "삼선교.한성대학교 승차 - 간선:140 - 간선:104 - 간선:160 - 지선:8101 - 원남동 하차",
            "distance": 2228,
            "time": 10,
            "routeColor": "0068B7",
            "routeStyle": "solid",
            "start": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "end": {
              "name": "원남동",
              "x": 126.99735555555556,
              "y": 37.57442777777778
            },
            "coordinates": [
              [
                127.009228,
                37.589908
              ],
              [
                127.006175,
                37.588575
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 735,
            "time": 9,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "원남동",
              "x": 126.99735555555556,
              "y": 37.57442777777778
            },
            "end": {
              "name": "도착지",
              "x": 126.99371186736082,
              "y": 37.57395392982104
            },
            "coordinates": [
              [
                126.997345,
                37.574425
              ],
              [
                126.99735,
                37.574284
              ]
            ]
          }
        ]
      },
      {
        "totalDistance": 3729,
        "totalTime": 33,
        "totalWalkTime": 15,
        "transferCount": 1,
        "fare": 1500,
        "pathType": 2,
        "detail": [
          {
            "mode": "도보",
            "timeline": "한양슈퍼앞까지 도보로 이동",
            "distance": 230,
            "time": 3,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "출발지",
              "x": 127.010225523923,
              "y": 37.5825624632779
            },
            "end": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "coordinates": [
              [
                127.0103,
                37.58257
              ],
              [
                127.010315,
                37.58246
              ],
              [
                127.01034,
                37.582386
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "한양슈퍼앞 승차 - 마을:성북02 - 성북세무서앞 하차",
            "distance": 509,
            "time": 4,
            "routeColor": "53B332",
            "routeStyle": "solid",
            "start": {
              "name": "한양슈퍼앞",
              "x": 127.01118333333334,
              "y": 37.58354722222222
            },
            "end": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "coordinates": [
              [
                127.011139,
                37.58355
              ],
              [
                127.011058,
                37.584642
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "삼선교.한성대학교까지 도보로 이동",
            "distance": 312,
            "time": 4,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "성북세무서앞",
              "x": 127.01057777777778,
              "y": 37.588094444444444
            },
            "end": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "coordinates": [
              [
                127.010578,
                37.588094
              ],
              [
                127.010469,
                37.588281
              ]
            ]
          },
          {
            "mode": "버스",
            "timeline": "삼선교.한성대학교 승차 - 간선:273 - 혜화경찰서 하차",
            "distance": 2405,
            "time": 13,
            "routeColor": "0068B7",
            "routeStyle": "solid",
            "start": {
              "name": "삼선교.한성대학교",
              "x": 127.00919166666667,
              "y": 37.589925
            },
            "end": {
              "name": "혜화경찰서",
              "x": 126.99749166666666,
              "y": 37.572652777777776
            },
            "coordinates": [
              [
                127.009228,
                37.589908
              ],
              [
                127.006175,
                37.588575
              ]
            ]
          },
          {
            "mode": "도보",
            "timeline": "도착지까지 도보로 이동",
            "distance": 569,
            "time": 8,
            "routeColor": "4D524C",
            "routeStyle": "dashed",
            "start": {
              "name": "혜화경찰서",
              "x": 126.99749166666666,
              "y": 37.572652777777776
            },
            "end": {
              "name": "도착지",
              "x": 126.99371186736082,
              "y": 37.57395392982104
            },
            "coordinates": [
              [
                126.99748,
                37.57265
              ],
              [
                126.99749,
                37.572582
              ]
            ]
          }
        ]
      }
    ]
  }
]
```