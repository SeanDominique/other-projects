# Import necessary libraries
import serial, scipy
import numpy as np
from pylsl import StreamInlet, resolve_stream
import socket
import os
import icecream as ic
import pygame
import random

# This program collects EEG data from an OpenBCI LSL stream and saves it to a .csv file

# Define experiment type, used in naming output files
experiment_type = "OE-CE"

def process_eeg_block(eeg_time_data, trial_type, trial_n):
    '''
    Saves the EEG data and associated timestamps to a .csv file.
    
    Parameters:
    eeg_time_data: A 2D numpy array containing EEG data and timestamps
    trial_type: An identifier for the type of trial currently running
    trial_n: The number of the trial currently running

    The function doesn't return anything. It simply writes the data to a file.
    '''
    # print(time_data.shape, eeg_data.shape)
    np.savetxt(experiment_type+"_trials/"+EXPERIMENT_N+"_"+str(trial_n)+".csv", eeg_time_data, delimiter=",")
    return


def read_eeg_data(window_size, Fs, Nch):
    '''
    Connects to the EEG data stream, collects data, and sends it to be saved.
    
    Parameters:
    window_size: The size of the data window to be collected, in milliseconds
    Fs: The sample rate of the EEG device, in Hz
    Nch: The number of channels (electrodes) in the EEG device
    
    The function doesn't return anything. It simply runs an infinite loop to collect data.
    '''
    
    trial_n = 1

    # Look for EEG data stream
    print("looking for an EEG stream...")
    streams = resolve_stream('type', 'EEG')
    print("Connected!!!!")
    inlet = StreamInlet(streams[0])

    # Calculate samples per window
    samples_per_window = Fs*window_size//1000

    # Initialize arrays for EEG data and timestamps
    eeg_data = np.zeros((samples_per_window, Nch))
    time_data = np.zeros((samples_per_window, 1))

    # Take in one data point to determine when the program actually starts collecting data from the data stream
    eeg_sample, start_time = inlet.pull_sample()
    eeg_data[0,:] = eeg_sample
    time_data[0] = start_time
    n_samples = 1

    # Record EEG data
    while True:
        print("Running...")
        
        # Get a new sample
        eeg_sample, timestamp = inlet.pull_sample()
        print("Connected")

        trial_type = '1'

        while n_samples < samples_per_window:
            # Get a new sample
            eeg_sample, timestamp = inlet.pull_sample()

            # Store samples
            eeg_data[n_samples,:] = eeg_sample
            time_data[n_samples] = timestamp
            n_samples += 1

            if n_samples >= samples_per_window:
                print((timestamp-start_time)*1000)
                
                time_window = time_data[:]
                eeg_window = eeg_data[:,:]

                # Concatenate time and eeg data
                eeg_time_window = np.concatenate((time_window, eeg_window), axis=1)
                process_eeg_block(eeg_time_window, trial_type, trial_n)
                
                start_time = timestamp
                trial_n += 1
                print("Stop")

        n_samples = 0


# Define server address and port
serverAddressPort   = ("127.0.0.1", 12345)
bufferSize = 1024

# Create a UDP socket at client side
UDPClientSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
UDPClientSocket.bind(serverAddressPort)
UDPClientSocket.setblocking(0)
EXPERIMENT_N = "4"

WINDOW_SIZE_MS = 9000
SAMPLE_RATE_HZ = 250
N_CH = 8

if __name__ == "__main__":
    read_eeg_data(WINDOW_SIZE_MS, SAMPLE_RATE_HZ, N_CH)