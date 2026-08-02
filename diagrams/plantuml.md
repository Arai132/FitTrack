# Use Case Diagram
```plantuml
left to right direction

actor User

rectangle "FitTrack" {

usecase "View Profile" as VP

usecase "Create Profile" as CP
usecase "Edit Profile" as EP
usecase "Delete Profile" as DP

usecase "Create Workout" as CW
usecase "Edit Workout" as EW
usecase "Delete Workout" as DW
usecase "View Workout" as VW

usecase "Create Exercise" as CE
usecase "View Exercise" as VE
usecase "Edit Exercise" as EE
usecase "Add Exercise\nto Workout" as AE
usecase "Remove Exercise\nfrom Workout" as RE
usecase "Delete Exercise" as DE

usecase "Start Workout" as SW
usecase "Track Current Workout" as TCW
usecase "Mark Set Complete" as MSC

usecase "View Workout History" as VWH
usecase "Search Workout History" as SWH
}

User --> VP
User --> CP

VP <.. EP : <<extend>>
EP <.. DP : <<extend>>

VP <.. VW : <<extend>>
VP <.. CW : <<extend>>
VW <.. EW : <<extend>>
VP <.. VE : <<extend>>
VE <.. EE : <<extend>>
EE <.. DE : <<extend>>

VP <.. CE : <<extend>>

EW <.. DW : <<extend>>
EW <.. AE : <<extend>>
EW <.. RE : <<extend>>

VP <.. SW : <<extend>>

VP <.. VWH : <<extend>>
VWH <.. SWH : <<extend>>

SW <.. TCW : <<extend>>
TCW ..> MSC : <<include>>

@enduml
```
