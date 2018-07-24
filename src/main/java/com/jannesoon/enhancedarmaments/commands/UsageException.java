package com.jannesoon.enhancedarmaments.commands;

import net.minecraft.command.CommandException;

public class UsageException extends CommandException
{
	public UsageException(String message) {
		super(message);
	}
}
