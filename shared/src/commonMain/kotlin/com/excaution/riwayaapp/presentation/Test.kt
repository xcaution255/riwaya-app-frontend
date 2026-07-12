package com.excaution.riwayaapp.presentation


/*
*  Presentation Layer (Unified Navigation & State Controller)
* Instead of dividing this into four bloated ViewModels,
* we manage user lifecycle state using a single AuthViewModel.
* We use an abstract AuthScreenState navigation screen flag to
* dictate which panel is currently visible to the composition tree.
* */

