package champak.champabun.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import champak.champabun.BaseFragment;

public class Adapter_ViewPager extends FragmentStatePagerAdapter
{
	private List <BaseFragment> fragments;
	private String [ ] titles;

	public Adapter_ViewPager( FragmentManager fm, String [ ] titles, List < BaseFragment > fragments )
	{
		super( fm );
		this.titles = titles;
		this.fragments = fragments;
	}

	public void UpdateTitle( String [ ] titles )
	{
		this.titles = titles;
		notifyDataSetChanged( );
	}

	@Override
	public Fragment getItem( int position )
	{
		return this.fragments.get( position );
	}

	@Override
	public int getCount( )
	{
		if ( titles == null )
		{
			return 0;
		}
		else
		{
			return titles.length;
		}
	}

	@Override
	public String getPageTitle( int position )
	{
		return titles [ position ];
	}

	public void setCurrentItem( int position )
	{
		// position = this.getCount()*100 + (position % this.fragments.size());
		this.setCurrentItem( position );
	}

	public void Update( )
	{
		for ( BaseFragment f : fragments )
		{
			if ( f != null )
			{
				f.Update( );
			}
		}
	}
}
