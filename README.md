# EEG folder contains:

__Built a brain computer interface (BCI) wheelchair system to control a wheel chair movement using motor imagery.__

### Signal_Processing
- EEG_Signal_Analysis_MENTALAB.py
  Process, Filter and Visualise EEG data

- EEG_signal_processing.ipynb
  Process, Filter, Visualise and classify EEG data for Error-related potential detection.
  
### Experiment_Data
- MI_trials
  EEG Data measured during an experiment performing motor imagery
- OE-CE_trials
  EEG Data measured during an experiment opening and closing eyes

### Experiment_Scripts
- circle_v3.ino
  Arduino script to control a mini prototype wheelchair wirelessly

- Get_IP.ino
  Arduino script to get IP and Port number from ESP32 arduino

- P300_Experiment.py
  Dynamic display for experiment to trigger P300 (type of EEG signal)

- Record_EEG.py
  Record and save EEG data using pylsl

- WC_Experiment_MENTALAB.py
  Script to run experiment to measure Motor Imagery signals and Error-related potentials. Controls wheelchair, dynamic display, and EEG data recording.

  
### Other

- DHF-webapp.tsx
  Webapp to record user input (scale from 1 to 10) in firebase, and link them to a survey. 

- OOP/project1-2
  Made a game to practice OOP.


